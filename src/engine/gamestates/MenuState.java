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
import engine.environment.Settings;
import engine.gui.GuiController;
import engine.gui.GuiElement;
import engine.gui.elements.SButton;
import engine.utils.Rect;
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
    
//    public GuiController_DEPRICATED gui;
    public GuiController gui;
    
    public static final int ID=0;
     
    @Override
    public int getID() {
        return ID;
    }
     
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        //much like constructor
        
//        gui = new GuiController_DEPRICATED ();
//        
//        //TEST BUTTON
//        GuiElement_DEPRICATED test = new GuiElement_DEPRICATED ();
//        
//        test.grfx = "gui_button";
//        test.x = 0;
//        test.y = 0;
//        test.text_font = "normal_font";
//        test.text_color = Color.black;
//        test.text = "Hello!";
//        test.onHover = () -> { test.text_color = Color.gray; test.grfx = "gui_button"; System.out.println("Mouse Hover"); };
//        test.onUnhover = () -> { test.text_color = Color.black; test.grfx = "gui_button"; System.out.println("Mouse Unhover"); };
//        test.onMouseDown = () -> { System.out.println("Mouse Down"); };
//        test.onMouseUp = () -> { ResMgr.sound_lib.get("click").play(1f, Settings.sfx_volume); System.out.println("Mouse Up"); };
//        
//        gui.addElement(test);
        
        gui = new GuiController ();
        
        SButton test = new SButton ("test_button_1");
        test.setSprite("gui_button");
        test.setHoverSprite("gui_button_hover");
        test.setClickSprite("gui_button");
        test.setTooltip("Hello There!");
        test.rect = new Rect (0,0,300,100);
        test.on_hover_trigger = "gui_test";
        test.on_unhover_trigger = "gui_test";
        test.on_mouse_up_trigger = "gui_test";
        test.on_mouse_down_trigger = "gui_test";
        gui.addElement(test);
        
        SButton test2 = new SButton ("test_button_2");
        test2.setSprite("gui_button");
        test2.setHoverSprite("gui_button_hover");
        test2.setClickSprite("gui_button");
        test2.setTooltip("Hello There Too!");
        test2.rect = new Rect (300,300,300,100);
        test2.on_hover_trigger = "gui_test";
        test2.on_unhover_trigger = "gui_test";
        test2.on_mouse_up_trigger = "gui_test";
        test2.on_mouse_down_trigger = "gui_test";
        gui.addElement(test2);
        
        SButton test3 = new SButton ("test_button_3");
        test3.setSprite("gui_button");
        test3.setHoverSprite("gui_button_hover");
        test3.setClickSprite("gui_button");
        test3.setTooltip("HU >:0");
        test3.rect = new Rect (350,350,300,300);
        test3.on_hover_trigger = "gui_test";
        test3.on_unhover_trigger = "gui_test";
        test3.on_mouse_up_trigger = "gui_test";
        test3.on_mouse_down_trigger = "gui_test";
        gui.addElement(test3);
    }
     
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
        //called on game's frame draw; PUT RENDERING/DRAWING CODE HERE
        
//        gui.draw();
        gui.render(gc, grphcs);
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
