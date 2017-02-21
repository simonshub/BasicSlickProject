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
import engine.gui.elements.SButton;
import engine.gui.elements.SLabel;
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
        
        int title_height = 96;
        int btn_height = 64;
        int btn_width = 256;
        int btn_margin = 48;
        
        SLabel titleLabel = ((SLabel) new SLabel ("title_label").setText("SOME BIG TITLE")
//                .setTextAlignment("center","center") // - doesn't do anything yet
                .setFont("title")
                .setRect(new Rect (0,0,Settings.screen_res_w,title_height))
                .setOverlay(1.0f, 1.0f, 1.0f, 0.3f));
        gui.addElement(titleLabel);
        
        SButton actorEditorBtn = (((SButton) new SButton ("actor_editor_btn")
                .setSprite("gui_button")
                .setHoverSprite("gui_button_hover")
                .setClickSprite("gui_button")
                .setRect(new Rect (Settings.screen_res_w/2-btn_width/2,title_height,btn_width,btn_height))
                .setOnHover("gui_test").setOnUnhover("gui_test")
                .setOnMouseUp("gui_test").setOnMouseDown("gui_test")
                )).setOnClick("click", "gui/go_to_actedit").setText("Actor Editor").setHotkey(Input.KEY_LCONTROL, Input.KEY_A);
        gui.addElement(actorEditorBtn);
        
        SButton entityEditorBtn = (((SButton) new SButton ("entity_editor_btn")
                .setSprite("gui_button")
                .setHoverSprite("gui_button_hover")
                .setClickSprite("gui_button")
                .setRect(new Rect (Settings.screen_res_w/2-btn_width/2,title_height+(btn_height+btn_margin),btn_width,btn_height))
                .setOnHover("gui_test").setOnUnhover("gui_test")
                .setOnMouseUp("gui_test").setOnMouseDown("gui_test")
                )).setOnClick("click", "gui/go_to_entedit").setText("Entity Editor").setHotkey(Input.KEY_LCONTROL, Input.KEY_S);
        gui.addElement(entityEditorBtn);
        
        SButton mapEditorBtn = ((SButton) (new SButton ("map_editor_btn")
                .setSprite("gui_button")
                .setHoverSprite("gui_button_hover")
                .setClickSprite("gui_button")
                .setRect(new Rect (Settings.screen_res_w/2-btn_width/2,title_height+(btn_height+btn_margin)*2,btn_width,btn_height))
                .setOnHover("gui_test").setOnUnhover("gui_test")
                .setOnMouseUp("gui_test").setOnMouseDown("gui_test")
                )).setOnClick("click", "gui/go_to_mapedit").setText("Map Editor").setHotkey(Input.KEY_LCONTROL, Input.KEY_D);
        gui.addElement(mapEditorBtn);
        
        SButton exitBtn = ((SButton) (new SButton ("exit_btn")
                .setSprite("gui_button")
                .setHoverSprite("gui_button_hover")
                .setClickSprite("gui_button")
                .setRect(new Rect (Settings.screen_res_w/2-btn_width/2,title_height+(btn_height+btn_margin)*3,btn_width,btn_height))
                .setOnHover("gui_test").setOnUnhover("gui_test")
                .setOnMouseUp("gui_test").setOnMouseDown("gui_test")
                )).setOnClick("click", "gui/exit").setText("Exit").setHotkey(Input.KEY_LCONTROL, Input.KEY_E);
        gui.addElement(exitBtn);
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
    }
     
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        ResMgr.sound_lib.get("song").play(1f, Settings.music_volume);
    }
    
}
