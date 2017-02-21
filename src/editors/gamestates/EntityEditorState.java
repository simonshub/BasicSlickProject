/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editors.gamestates;

import editors.toolbars.EntityEditorToolbar;
import engine.environment.Data;
import engine.game.actors.Actor;
import engine.game.entities.Collider;
import engine.game.entities.EntityType;
import engine.logger.Log;
import engine.environment.ResMgr;
import engine.gamestates.MenuState;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Editor State class which provides Slick2D Entity Editor functionality.
 * @author XyRoN (Emil SimoN)
 */
public class EntityEditorState extends BasicGameState {

    /**
     * Entity Editor State ID (101).
     */
    public static final int ID=101;
    
    /**
     * The toolbar provided upon Entity Editor State entry, contains various tools and selectors.
     */
    public EntityEditorToolbar entity_toolbar = null;
    
    /**
     * Returns this Editor State's ID.
     * @return The Entity Editor State's ID (101)
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
    }
    
    /**
     * Renders the State's graphics.
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @param g The given Slick2D Graphcis
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        //called on game's frame draw; PUT RENDERING/DRAWING CODE HERE
        if (entity_toolbar != null) {
            if (ResMgr.hasEntityType(entity_toolbar.currentEntityName)) {
                if (ResMgr.hasActor(entity_toolbar.currentEntity.actor_name)) {
                    EntityType e = entity_toolbar.currentEntity;
                    Actor a = ResMgr.getActor(e.actor_name);
                    
                    a.render(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2, gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2, Color.white, a.default_anim, 1f);
                    g.setColor(new Color (255,255,255,150));
                    g.drawRect(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2, gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2, ResMgr.sprite_lib.get(a.sheet).dimX, ResMgr.sprite_lib.get(a.sheet).dimY);
                    
                    g.setColor(new Color (255,0,0,100));
                    if (e.collider.state == Collider.ColliderState.BOX) {
                        g.drawRect(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX - e.collider.box_width/2,
                                   gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY - e.collider.box_height/2,
                                   e.collider.box_width, e.collider.box_height);
                        g.fillRect(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX - e.collider.box_width/2,
                                   gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY - e.collider.box_height/2,
                                   e.collider.box_width, e.collider.box_height);
                    } else if (e.collider.state == Collider.ColliderState.RADIAL) {
                        g.drawOval(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX - e.collider.radius,
                                   gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY - e.collider.radius,
                                   e.collider.radius*2, e.collider.radius*2);
                        g.fillOval(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX - e.collider.radius,
                                   gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY - e.collider.radius,
                                   e.collider.radius*2, e.collider.radius*2);
                    }
                    g.setColor(new Color (255,255,255,255));
                    g.fillOval(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY,
                               2, 2);
                    g.setColor(new Color (255,255,255,150));
                    g.drawLine(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2,
                               gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY);
                    g.drawLine(gc.getWidth()/2+ResMgr.sprite_lib.get(a.sheet).dimX/2,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2,
                               gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY);
                    g.drawLine(gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2,
                               gc.getHeight()/2+ResMgr.sprite_lib.get(a.sheet).dimY/2,
                               gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY);
                    g.drawLine(gc.getWidth()/2+ResMgr.sprite_lib.get(a.sheet).dimX/2,
                               gc.getHeight()/2+ResMgr.sprite_lib.get(a.sheet).dimY/2,
                               gc.getWidth()/2-ResMgr.sprite_lib.get(a.sheet).dimX/2 + e.originX,
                               gc.getHeight()/2-ResMgr.sprite_lib.get(a.sheet).dimY/2 + e.originY);
                }
            }
        }
    }
    
    /**
     * Updates the State's logic.
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @param dt The given Slick2D delta time
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) throws SlickException {
        if (gc.getInput().isKeyDown(Input.KEY_ESCAPE)) {
            entity_toolbar.dispose();
            entity_toolbar = null;
            Data.changeState(MenuState.ID);
        }
        
        if (entity_toolbar != null) {
            if (ResMgr.hasEntityType(entity_toolbar.currentEntityName)) {
                if (ResMgr.hasActor(entity_toolbar.currentEntity.actor_name)) {
                    if (gc.getInput().isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
                        EntityType e = entity_toolbar.currentEntity;
                        Actor a = ResMgr.getActor(e.actor_name);

                        int clickX = gc.getInput().getMouseX() - gc.getWidth()/2 + ResMgr.sprite_lib.get(a.sheet).dimX/2;
                        int clickY = gc.getInput().getMouseY() - gc.getHeight()/2 + ResMgr.sprite_lib.get(a.sheet).dimY/2;
                        
                        entity_toolbar.setOrigin(clickX, clickY);
                    }
                }
            }
        }
    }
    
    /**
     * Overridden enter method, built on to initialize the Entity Editor Toolbar
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @throws SlickException
     */
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        
        try {
            entity_toolbar = new EntityEditorToolbar ();
            Thread t = new Thread ( () -> {
                entity_toolbar.setVisible(true);
            });
            t.start();
        } catch (Exception e) {
            Log.err(Log.ENTITY,"while trying to create entity editor toolbar",e);
            e.printStackTrace();
        }
    }
    
}
