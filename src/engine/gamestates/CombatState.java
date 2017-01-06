/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.gamestates;

import engine.game.triggers.TriggerMgr;
import engine.gui.GuiController_DEPRICATED;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author XyRoN (Emil SimoN)
 */
public class CombatState extends BasicGameState {
    public static final int ID=1;
    public GuiController_DEPRICATED gui;
    
    @Override
    public int getID() {
        return ID;
    }
     
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        //much like constructor
        
        gui = new GuiController_DEPRICATED ();
        
        
        //add GUI elements here like so;
        /*gui.addElement(new GuiElement (x, y, idleImgPath, mouseOverImgPath, mouseClickImgPath, soundPath,
                new Callable () {
                    @Override
                    public void call() {
                        //ON-CLICK CODE GOES HERE!
                    }
                }
        ));*/
        
        
        /*gui.addElement(new GuiElement (0, 0, "", "", "", "",
                new Callable () {
                    @Override
                    public void call() {
                        //ON-CLICK CODE GOES HERE!
                    }
                }
        ));*/
        
    }
     
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
        //called on game's frame draw; PUT RENDERING/DRAWING CODE HERE
        
        gui.draw();
    }
     
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
        //called on game's logical update loop; PUT GAME/LOGIC CODE HERE
        
        gui.update(gc);
    }
    
}
