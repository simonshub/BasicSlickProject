/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package editors.gamestates;

import editors.toolbars.MapEditorToolbar;
import engine.game.maps.GameMap;
import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.logger.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Emil Simon
 */

public class MapEditorState extends BasicGameState {
    public static final int ID=102;
     
    public enum Tool {
        None, TilePaint, TileErase, EntityPaint, EntityErase, EntityModify };
     
    public GameMap currentMap;
    public MapEditorToolbar map_toolbar;
    public int dragX,dragY;
    public int dragOriginX,dragOriginY;
    public int tilePainterX,tilePainterY;

    /**
     *
     * @return
     */
    @Override
    public int getID() {
        return ID;
    }
     
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        currentMap = new GameMap ("untitled",100,100,false);
        dragX=-1;
        dragY=-1;
        dragOriginX=-1;
        dragOriginY=-1;
        tilePainterX=-1;
        tilePainterY=-1;
    }
     
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        currentMap.render(gc,sbg,g);
        currentMap.drawInfo(gc,sbg,g);
        
        if ((tilePainterX>=0) && (tilePainterX<=currentMap.tiles_width) && (tilePainterY>=0) && (tilePainterY<=currentMap.tiles_height)) {
            g.setColor(new Color (1f,1f,1f,0.5f));
            g.fillRect(tilePainterX*Consts.TILESET_FRAME_WIDTH - currentMap.cam.location.x - (Consts.TILESET_FRAME_WIDTH / 2),
                       tilePainterY*Consts.TILESET_FRAME_HEIGHT - currentMap.cam.location.y - (Consts.TILESET_FRAME_HEIGHT / 2), 
                       Consts.TILESET_FRAME_WIDTH, Consts.TILESET_FRAME_HEIGHT);
            g.setColor(Color.white);
            //g.drawString("Selector: "+tilePainterX+","+tilePainterY, 32, 128);
        } else if (map_toolbar.editMode == map_toolbar.editMode.ENTITIES) {
            ResMgr.getActor(map_toolbar.getSelectedEntityType().actor_name)
                    .render(gc.getInput().getMouseX(), gc.getInput().getMouseY(), new Color (1f,1f,1f,0.5f),
                            ResMgr.getActor(map_toolbar.getSelectedEntityType().actor_name).default_anim);
        }
        
    }
     
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
        switch (map_toolbar.editMode) {
            case TILES :
                currentMap.devmode_current_tileset = map_toolbar.getCurrentTileset();
                currentMap.background_tileset = map_toolbar.getCurrentBackgroundTileset();
                break;
            case ENTITIES :
                currentMap.devmode_current_tileset = null;
                break;
            default :
                break;
        }
        
        //called on game's logical update loop; PUT GAME/LOGIC CODE HERE
        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_A)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(ActorEditorState.ID);
        }

        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_E)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(EntityEditorState.ID);
        }

        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_M)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(MapEditorState.ID);
        }

        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_T)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(ActorEditorState.ID);
        }
        
        if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            tilePainterX = -1;
            tilePainterY = -1;
            
            if ((dragX<0) || (dragY<0)) {
                dragX = gc.getInput().getMouseX();
                dragY = gc.getInput().getMouseY();
                
                dragOriginX = currentMap.cam.location.x;
                dragOriginY = currentMap.cam.location.y;
            } else {
                currentMap.cam.location.x = dragOriginX + (dragX - gc.getInput().getMouseX());
                currentMap.cam.location.y = dragOriginY + (dragY - gc.getInput().getMouseY());
            }
        } else {
            dragX = -1;
            dragY = -1;
            dragOriginX = -1;
            dragOriginY = -1;
            
            if (map_toolbar.editMode == MapEditorToolbar.EditMode.TILES) {
                tilePainterX = ((gc.getInput().getMouseX() + (Consts.TILESET_FRAME_WIDTH/2) + currentMap.cam.location.x) / Consts.TILESET_FRAME_WIDTH);
                tilePainterY = ((gc.getInput().getMouseY() + (Consts.TILESET_FRAME_HEIGHT/2) + currentMap.cam.location.y) / Consts.TILESET_FRAME_HEIGHT);
            } else {
                tilePainterX = -1;
                tilePainterY = -1;
            }
        }
        
        if ((map_toolbar.editMode == MapEditorToolbar.EditMode.TILES) && (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))) {
            if ((tilePainterX>=0) && (tilePainterX<=currentMap.tiles_width)
                    && (tilePainterY>=0) && (tilePainterY<=currentMap.tiles_height)
                    && (currentMap.devmode_current_tileset!=null)) {
                switch (map_toolbar.tilesetTool) {
                    case PAINT :
                        currentMap.tile_net.map[tilePainterY][tilePainterX] = currentMap.tile_net.getTilesetId(currentMap.devmode_current_tileset);
                        break;
                    case FILL :
                        currentMap.tile_net.fill(tilePainterX, tilePainterY, currentMap.devmode_current_tileset);
                        break;
                    default :
                        break;
                }
            }
        }
        
        if ((map_toolbar.editMode == MapEditorToolbar.EditMode.ENTITIES) && (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))) {
            currentMap.placeEntity(map_toolbar.getSelectedEntityType(),
                    (gc.getInput().getMouseX() + currentMap.cam.location.x),
                    (gc.getInput().getMouseY() + currentMap.cam.location.y));
        }
    }
     
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        
        try {
            map_toolbar = new MapEditorToolbar ();
            Thread t = new Thread ( () -> {
                map_toolbar.setVisible(true);
            });
            t.start();
        } catch (Exception e) {
            Log.err(Log.MAP,"while trying to create map editor toolbar",e);
            e.printStackTrace();
        }
    }
}
