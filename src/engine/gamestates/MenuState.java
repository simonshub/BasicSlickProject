/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.gamestates;

import editors.gamestates.ActorEditorState;
import editors.gamestates.EntityEditorState;
import editors.gamestates.MapEditorState;
import engine.environment.ResMgr;
import engine.gui.GuiController;
import engine.gui.GuiElement;
import engine.environment.Settings;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author XyRoN (Emil SimoN)
 */
public class MenuState extends BasicGameState {
    public enum Substate { Main, NewGame, LoadGame, Options, ExitConfirm };
    public Substate current_context = Substate.Main;
    
    public GuiController gui;
    
    public static final int ID=0;
     
    @Override
    public int getID() {
        return ID;
    }
     
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        //much like constructor
        
        gui = new GuiController ();
        
        //TEST BUTTON
        GuiElement test = new GuiElement ();
        
        test.grfx = "gui_button";
        test.x = 0;
        test.y = 0;
        test.text_font = "normal_font";
        test.text_color = Color.black;
        test.text = "Hello!";
        test.onHover = () -> { test.text_color = Color.gray; test.grfx = "gui_button_hover"; System.out.println("Mouse Hover"); };
        test.onUnhover = () -> { test.text_color = Color.black; test.grfx = "gui_button"; System.out.println("Mouse Unhover"); };
        test.onMouseDown = () -> { System.out.println("Mouse Down"); };
        test.onMouseUp = () -> { ResMgr.sound_lib.get("click").play(1f, Settings.sfx_volume); System.out.println("Mouse Up"); };
        
        gui.addElement(test);
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
        
        if (Settings.devmode) {
            if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_A))
                sbg.enterState(ActorEditorState.ID);

            if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_E))
                sbg.enterState(EntityEditorState.ID);

            if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_M))
                sbg.enterState(MapEditorState.ID);

            if (gc.getInput().isKeyDown(Input.KEY_LCONTROL) && gc.getInput().isKeyPressed(Input.KEY_T))
                sbg.enterState(ActorEditorState.ID);
        }
    }
     
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        ResMgr.sound_lib.get("song").play(1f, Settings.music_volume);
    }
    
}
