/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
 
package editors.gamestates;

import editors.toolbars.GuiEditorToolbar;
import engine.logger.Log;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Editor State class which provides Slick2D Actor Editor functionality.
 * @author XyRoN (Emil SimoN)
 */
public class GuiEditorState extends BasicGameState {

    /**
     * An enum for determining the GUI Editor's current substate - whether it is in edit or preview mode.
     */
    public enum Substate { NewElement, DeleteElement, SelectElement };
    
    /**
     * Actor Editor State ID (100).
     */
    public static final int ID=103;
    
    /**
     * The toolbar provided upon Actor Editor State entry, contains various tools and selectors.
     */
    public GuiEditorToolbar gui_toolbar = null;

    /**
     * The Actor Editor's current substate - SpriteSheet (edit mode), PreviewAnim (preview mode).
     */
    public Substate state;
    
    
    
    /**
     * Returns this Editor State's ID.
     * @return The GUI Editor State's ID (103)
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
     * Renders the State's graphics, relative to it's current substate.
     * @param gc The given Slick2D GameContainer
     * @param sbg The given Slick2D StateBasedGame
     * @param g The given Slick2D Graphcis
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        //called on game's frame draw; PUT RENDERING/DRAWING CODE HERE
        
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
        //called on game's logical update loop; PUT GAME/LOGIC CODE HERE
        
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
            gui_toolbar = new GuiEditorToolbar ();
            Thread t = new Thread ( () -> {
                gui_toolbar.setVisible(true);
            });
            t.start();
        } catch (Exception e) {
            Log.err(Log.GENERAL,"while trying to create gui editor toolbar",e);
            e.printStackTrace();
        }
    }
}
