/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
 
package editors.gamestates;

import editors.toolbars.ActorEditorToolbar;
import engine.environment.Data;
import engine.game.actors.AnimFrame;
import engine.logger.Log;
import engine.environment.ResMgr;
import engine.gamestates.MenuState;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Editor State class which provides Slick2D Actor Editor functionality.
 * @author XyRoN (Emil SimoN)
 */
public class ActorEditorState extends BasicGameState {

    /**
     * An enum for determining the Actor Editor's current substate - whether it is in edit or preview mode.
     */
    public enum Substate { 

        /**
         * Actor Editor's edit mode.
         */
        SpriteSheet, 

        /**
         * Actor Editor's preview mode.
         */
        PreviewAnim };
    
    /**
     * Actor Editor State ID (100).
     */
    public static final int ID=100;
    
    /**
     * The toolbar provided upon Actor Editor State entry, contains various tools and selectors.
     */
    public ActorEditorToolbar actor_toolbar = null;

    /**
     * The Actor Editor's current substate - SpriteSheet (edit mode), PreviewAnim (preview mode).
     */
    public Substate state;

    /**
     * The calltag of the currently loaded Actor.
     */
    public String loadedActor;

    /**
     * The calltag of the currently loaded SpriteSheet.
     */
    public String loadedSprite;

    /**
     * The calltag of the currently loaded Animation.
     */
    public String loadedAnimName;

    /**
     * The currently loaded Animation.
     */
    public Animation loadedAnim;

    /**
     * The X coordinate of the loaded Actor's origin point.
     */
    public int originX;

    /**
     * The Y coordinate of the loaded Actor's origin point.
     */
    public int originY;

    /**
     * The X coordinate of the origin grab point.
     */
    public int originGrabX;

    /**
     * The Y coordinate of the origin grab point.
     */
     public int originGrabY;

    /**
     * The X coordinate of the camera point. This point determines the rendering offset of the Editor.
     */
    public int camX;

    /**
     * The Y coordinate of the camera point. This point determines the rendering offset of the Editor.
     */
    public int camY;

    /**
     * The X coordinate of the camera grab point.
     */
    public int camGrabX;

    /**
     * The Y coordinate of the camera grab point.
     */
    public int camGrabY;

    /**
     * The currently selected frame's X grid location.
     */
    public int selX;

    /**
     * The currently selected frame's Y grid location.
     */
    public int selY;

    /**
     * The currently loaded Sprite Sheet's X frame dimension.
     */
    public int dimX;

    /**
     * The currently loaded Sprite Sheet's Y frame dimension.
     */
    public int dimY;

    /**
     * The X coordinate for the drawing point of the Actor's preview in preview mode.
     */
    public int previewX;

    /**
     * The Y coordinate for the drawing point of the Actor's preview in preview mode.
     */
    public int previewY;
    
    /**
     * Returns this Editor State's ID.
     * @return The Actor Editor State's ID (100)
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Initializer method.
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        camX = 0;
        camY = 0;
        camGrabX = -1;
        camGrabY = -1;
        originX = 100;
        originY = 100;
        originGrabX = 0;
        originGrabY = 0;
        selX = 0;
        selY = 0;
        dimX = 32;
        dimY = 32;
        previewX = gc.getWidth()/2;
        previewY = gc.getHeight()/2;
        loadedActor = "";
        loadedSprite = "";
        state = Substate.SpriteSheet;
    }
    
    /**
     * Renders the State's graphics, relative to it's current substate.
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @param g The given Slick2D Graphcis
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        //called on game's frame draw; PUT RENDERING/DRAWING CODE HERE
        if ((ResMgr.getActor(loadedActor) != null) && (ResMgr.getSprite(loadedSprite) != null)) {
            if (state == Substate.SpriteSheet) {
                ResMgr.getSprite(loadedSprite).draw(originX,originY);
            } else if (state == Substate.PreviewAnim) {
                //ResMgr.getActor(loadedActor).anims.get(actor_toolbar.currentAnim).draw(previewX, previewY);
                loadedAnim.draw(previewX, previewY);
            }
            
            if (actor_toolbar!=null) {
                if (actor_toolbar.gridlines && (state==Substate.SpriteSheet)) {
                    int linesX, linesY;
                    int offsetX, offsetY;
                    
                    offsetX = originX % dimX;
                    offsetY = originY % dimY;
                    linesX = (int) (gc.getWidth() / dimX) +1;
                    linesY = (int) (gc.getHeight() / dimY) +1;
                    
                    g.setColor(new Color (255,255,255,100));
                    for (;linesX>=0;linesX--) {
                        //vertical gridlines
                        g.drawLine(offsetX+linesX*dimX, 0, offsetX+linesX*dimX, gc.getHeight());
                    }
                    for (;linesY>=0;linesY--) {
                        //vertical gridlines
                        g.drawLine(0, offsetY+linesY*dimY, gc.getWidth(), offsetY+linesY*dimY);
                    }
                }
            }
        }
        
        if (state == Substate.SpriteSheet) {
            g.setColor(new Color (255,255,255,100));
            g.fillRect(originX+selX*dimX, originY+selY*dimY, dimX, dimY);
        }
    }
    
    /**
     * Updates the State's logic, relative to it's current substate.
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @param dt The given Slick2D delta time
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) throws SlickException {
        if (gc.getInput().isKeyDown(Input.KEY_ESCAPE)) {
            actor_toolbar.dispose();
            actor_toolbar = null;
            Data.changeState(MenuState.ID);
        }
        
        if (actor_toolbar != null) {
            if (!actor_toolbar.currentActorName.isEmpty()) {
                if (!actor_toolbar.currentActorName.equals(this.loadedActor) || !actor_toolbar.currentActor.sheet.equals(this.loadedSprite)) {
                    this.loadedActor = actor_toolbar.currentActor.actor_name;
                    this.loadedSprite = actor_toolbar.currentActor.sheet;
                    dimX = ResMgr.getAnimatedSprite(loadedSprite).dimX;
                    dimY = ResMgr.getAnimatedSprite(loadedSprite).dimY;
                }
            }
            
            Substate old = state;
            state = actor_toolbar.previewAnim ? Substate.PreviewAnim : Substate.SpriteSheet ;
            if (((state==Substate.PreviewAnim) && (old==Substate.SpriteSheet)) || (!actor_toolbar.currentAnim.equals(loadedAnimName))) {
                loadedAnimName = actor_toolbar.currentAnim;
                loadedAnim = new Animation ();
                AnimFrame[] frames = new AnimFrame [ResMgr.getActor(loadedActor).anim_frame_list.size()];
                ResMgr.getActor(loadedActor).anim_frame_list.toArray(frames);
                for (int i=0;i<frames.length;i++) {
                    if (frames[i].animation.equals(actor_toolbar.currentAnim))
                        loadedAnim.addFrame(ResMgr.getSprite(loadedSprite).getSubImage(frames[i].x,frames[i].y) , frames[i].dur);
                }
            }
        }
        
        switch (state) {
            case SpriteSheet :
                if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
                    camX = gc.getInput().getMouseX();
                    camY = gc.getInput().getMouseY();

                    if (camGrabX < 0) {
                        camGrabX = gc.getInput().getMouseX();
                        originGrabX = originX;
                    }
                    if (camGrabY < 0) {
                        camGrabY = gc.getInput().getMouseY();
                        originGrabY = originY;
                    }

                    originX = originGrabX+(camX-camGrabX);
                    originY = originGrabY+(camY-camGrabY);
                } else {
                    camGrabX = -1;
                    camGrabY = -1;
                }
                
                if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
                    selX = Math.max(0, Math.min(ResMgr.getSprite(loadedSprite).getHorizontalCount()-1, (int)((gc.getInput().getMouseX()-originX)/dimX) ));
                    selY = Math.max(0, Math.min(ResMgr.getSprite(loadedSprite).getVerticalCount()-1, (int)((gc.getInput().getMouseY()-originY)/dimY) ));
                    actor_toolbar.setSelectedFrame(selX, selY);
                }
                
                break;
                
            case PreviewAnim :
                if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
                    previewX = gc.getInput().getMouseX() - (int)(dimX/2);
                    previewY = gc.getInput().getMouseY() - dimY;
                }
                
                break;
        }
    }
    
    /**
     * Overridden enter method, built on to initialize the Actor Editor Toolbar
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @throws SlickException
     */
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        
        try {
            actor_toolbar = new ActorEditorToolbar ();
            Thread t = new Thread ( () -> {
                actor_toolbar.setVisible(true);
            });
            t.start();
        } catch (Exception e) {
            Log.err(Log.ACTOR,"while trying to create actor editor toolbar",e);
            e.printStackTrace();
        }
    }
}
