/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package editors.gamestates;

import editors.toolbars.MapTrigEditorToolbar;
import engine.game.maps.GameMap;
import engine.environment.Consts;
import engine.environment.Data;
import engine.environment.ResMgr;
import engine.environment.Settings;
import engine.game.entities.EntityType;
import engine.logger.Log;
import java.io.File;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Emil Simon
 */

public class MapEditorState extends BasicGameState implements MouseListener {
    public static final int ID=102;
     
    public GameMap currentMap;
    public MapTrigEditorToolbar map_toolbar;
    public int dragX,dragY;
    public int dragOriginX,dragOriginY;
    public int tilePainterX,tilePainterY;
    public boolean snapToGrid;
    
    

    @Override
    public int getID() {
        return ID;
    }
     
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (Settings.editor_autoload_mapname.isEmpty()) {
            currentMap = new GameMap ("untitled",100,100,false);
        } else {
            currentMap = new GameMap (new File (Consts.MAP_DUMP_FOLDER+Settings.editor_autoload_mapname));
        }
        dragX=-1;
        dragY=-1;
        dragOriginX=-1;
        dragOriginY=-1;
        tilePainterX=-1;
        tilePainterY=-1;
        snapToGrid = false;
    }
    
    
    
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        currentMap.render(gc,sbg,g,true);
        
        if (map_toolbar==null)
            return;
        
        if (map_toolbar.entityTool == MapTrigEditorToolbar.EntityTool.DELETE) {
            if (currentMap.getMouseOverEntity(gc) != null)
                currentMap.getMouseOverEntity(gc).renderWithFilter(gc, sbg, g, currentMap.cam, new Color (1f,0f,0f,0.5f));
        } else if (map_toolbar.entityTool == MapTrigEditorToolbar.EntityTool.EDIT) {
            if (currentMap.getMouseOverEntity(gc) != null)
                currentMap.getMouseOverEntity(gc).renderWithFilter(gc, sbg, g, currentMap.cam, new Color (0f,1f,0f,0.5f));
        }
        
        currentMap.drawInfo(gc,sbg,g);
        
        if ((tilePainterX>=0) && (tilePainterX<=currentMap.tiles_width) && (tilePainterY>=0) && (tilePainterY<=currentMap.tiles_height)) {
            g.setColor(new Color (1f,1f,1f,0.5f));
            g.fillRect(tilePainterX*Consts.TILESET_FRAME_WIDTH - currentMap.cam.location.x - (Consts.TILESET_FRAME_WIDTH / 2),
                       tilePainterY*Consts.TILESET_FRAME_HEIGHT - currentMap.cam.location.y - (Consts.TILESET_FRAME_HEIGHT / 2), 
                       Consts.TILESET_FRAME_WIDTH, Consts.TILESET_FRAME_HEIGHT);
            g.setColor(Color.white);
            //g.drawString("Selector: "+tilePainterX+","+tilePainterY, 32, 128);
        } else if ((map_toolbar.editMode == MapTrigEditorToolbar.EditMode.ENTITIES) && (map_toolbar.entityTool == MapTrigEditorToolbar.EntityTool.PLACE)) {
            int mouse_x = gc.getInput().getMouseX();
            int mouse_y = gc.getInput().getMouseY();
            if (snapToGrid) {
                mouse_x -= gc.getInput().getMouseX()%map_toolbar.getGridX()
                        - (gc.getInput().getMouseX()%map_toolbar.getGridX() > map_toolbar.getGridX()/2 ? map_toolbar.getGridX() : 0);
                mouse_y -= gc.getInput().getMouseY()%map_toolbar.getGridY()
                        - (gc.getInput().getMouseY()%map_toolbar.getGridY() > map_toolbar.getGridY()/2 ? map_toolbar.getGridY() : 0);
            }
            
            EntityType cur_entity = map_toolbar.getSelectedEntityType();
            Color filter = new Color (1f,1f,1f,0.5f);
            Color collider_filter = new Color (0f,1f,0f,0.5f);
            
            if (!currentMap.canPlaceEntity(cur_entity,
                                         (mouse_x + currentMap.cam.location.x),
                                         (mouse_y + currentMap.cam.location.y), currentMap.getBounds())) {
                filter = new Color (1f,0f,0f,0.5f);
                collider_filter = new Color (1f,0f,0f,0.5f);
            }
            
            cur_entity.getActor().render(mouse_x-cur_entity.originX,
                                         mouse_y-cur_entity.originY,
                                         filter, cur_entity.getActor().default_anim, currentMap.cam.zoom);
            
            cur_entity.collider.renderExplicit(g, mouse_x, mouse_y, collider_filter);
        }
        
    }
     
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
        if (map_toolbar.play_test) {
            Data.loadMap(currentMap);
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(MapTestState.ID);
            return;
        }
        
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
        
        if ((map_toolbar.currentlySelectedEntity != null) && (!map_toolbar.currentlySelectedEntity.name.equals(map_toolbar.oldEntityName))) {
            Log.log(Log.MAP, "changed entity '"+map_toolbar.oldEntityName+"' name to '"+map_toolbar.getNewName()+"'");
            currentMap.changeEntityName(map_toolbar.oldEntityName, map_toolbar.getNewName());
            map_toolbar.oldEntityName = map_toolbar.getNewName();
        }
        
        //called on game's logical update loop; PUT GAME/LOGIC CODE HERE
        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_A)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(ActorEditorState.ID);
            return;
        }

        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_E)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(EntityEditorState.ID);
            return;
        }

        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_M)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(MapEditorState.ID);
            return;
        }

        if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_T)) {
            map_toolbar.dispose();
            map_toolbar = null;
            sbg.enterState(ActorEditorState.ID);
            return;
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
            
            if (map_toolbar.editMode == MapTrigEditorToolbar.EditMode.TILES) {
                tilePainterX = ((gc.getInput().getMouseX() + (Consts.TILESET_FRAME_WIDTH/2) + currentMap.cam.location.x) / Consts.TILESET_FRAME_WIDTH);
                tilePainterY = ((gc.getInput().getMouseY() + (Consts.TILESET_FRAME_HEIGHT/2) + currentMap.cam.location.y) / Consts.TILESET_FRAME_HEIGHT);
            } else {
                tilePainterX = -1;
                tilePainterY = -1;
            }
        }
        
        if ((map_toolbar.editMode == MapTrigEditorToolbar.EditMode.TILES) && (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))) {
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
        
        if ((map_toolbar.editMode == MapTrigEditorToolbar.EditMode.ENTITIES) && (map_toolbar.entityTool == MapTrigEditorToolbar.EntityTool.PLACE) && (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))) {
            snapToGrid = map_toolbar.getSnapToGrid();
            
            int mouse_x = gc.getInput().getMouseX();
            int mouse_y = gc.getInput().getMouseY();
            if (snapToGrid) {
                mouse_x -= gc.getInput().getMouseX()%map_toolbar.getGridX()
                        - (gc.getInput().getMouseX()%map_toolbar.getGridX() > map_toolbar.getGridX()/2 ? map_toolbar.getGridX() : 0);
                mouse_y -= gc.getInput().getMouseY()%map_toolbar.getGridY()
                        - (gc.getInput().getMouseY()%map_toolbar.getGridY() > map_toolbar.getGridY()/2 ? map_toolbar.getGridY() : 0);
            }
            if (currentMap.canPlaceEntity(map_toolbar.getSelectedEntityType(),
                                         (mouse_x + currentMap.cam.location.x),
                                         (mouse_y + currentMap.cam.location.y), currentMap.getBounds()))
                currentMap.placeEntity(map_toolbar.getSelectedEntityType(), mouse_x + currentMap.cam.location.x, mouse_y + currentMap.cam.location.y);
        }
        
        if ((map_toolbar.editMode == MapTrigEditorToolbar.EditMode.ENTITIES) && (map_toolbar.entityTool == MapTrigEditorToolbar.EntityTool.EDIT) && (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))) {
            if (currentMap.getMouseOverEntity(gc)!=null) {
                map_toolbar.setSelectedEntity(currentMap.getMouseOverEntity(gc));
            } else if (map_toolbar.currentlySelectedEntity!=null) {
                map_toolbar.currentlySelectedEntity.moveToWithCollisionDetection(currentMap.entities,
                            gc.getInput().getMouseX()+currentMap.cam.location.x, gc.getInput().getMouseY()+currentMap.cam.location.y);
            }
        }
        
        if ((map_toolbar.editMode == MapTrigEditorToolbar.EditMode.ENTITIES) && (map_toolbar.entityTool == MapTrigEditorToolbar.EntityTool.DELETE) && (gc.getInput().isMousePressed(Input.MOUSE_LEFT_BUTTON))) {
            currentMap.destroyEntity(currentMap.getMouseOverEntity(gc));
        }
        
        if (map_toolbar.save) {
            currentMap.save();
            map_toolbar.save = false;
        }
        
        if (map_toolbar.triggers_changed) {
            currentMap.trigger_store.clear();
            for (String trig : map_toolbar.triggerNamesList)
                if (ResMgr.hasTrigger(trig))
                    currentMap.trigger_store.put(trig, ResMgr.getTrigger(trig));
            map_toolbar.triggers_changed = false;
            map_toolbar.updateTriggerList(map_toolbar.triggerNamesList.toArray(new String [currentMap.trigger_store.keySet().size()]));
        }
        
        if (!map_toolbar.load.isEmpty()) {
            currentMap = new GameMap (new File (map_toolbar.load));
            
            map_toolbar.setBackgroundTileset(currentMap.background_tileset);
            map_toolbar.triggerNamesList.clear();
            for (String trig : currentMap.trigger_store.keySet()) {
                map_toolbar.triggerNamesList.add(trig);
                map_toolbar.updateTriggerList(map_toolbar.triggerNamesList.toArray(new String [currentMap.trigger_store.keySet().size()]));
            }
            
            map_toolbar.load = "";
            map_toolbar.updateTriggerList(map_toolbar.triggerNamesList.toArray(new String [currentMap.trigger_store.keySet().size()]));
        }
    }
    
    
    
//    @Override
//    public void mouseWheelMoved (int change) {
//        currentMap.cam.zoom += change/100;
//    }
    
    
     
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        
        try {
            map_toolbar = new MapTrigEditorToolbar ();
            Thread t = new Thread ( () -> {
                map_toolbar.setVisible(true);
            });
            t.start();
        } catch (Exception e) {
            Log.err(Log.MAP,"while trying to create map editor toolbar",e);
            e.printStackTrace();
        }
        
//        if (Settings.editor_autoload_mapname.isEmpty()) {
//            currentMap = new GameMap ("untitled",100,100,false);
//        } else {
            currentMap = new GameMap (new File (Consts.MAP_DUMP_FOLDER+Settings.editor_autoload_mapname));
//        }
        dragX=-1;
        dragY=-1;
        dragOriginX=-1;
        dragOriginY=-1;
        tilePainterX=-1;
        tilePainterY=-1;
        snapToGrid = false;
    }
}
