/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editors.gamestates;

import editors.toolbars.ScriptConsole;
import engine.environment.Data;
import engine.environment.Settings;
import engine.game.triggers.TriggerMgr;
import engine.gui.GuiController;
import engine.logger.Log;
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
public class MapTestState extends BasicGameState {
    public static final int ID=104;
    public GuiController gui;
    
    public ScriptConsole script_console;
    
    
    
    @Override
    public int getID() {
        return ID;
    }
     
    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        //much like constructor
        
        gui = new GuiController ();
    }
     
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
        //called on game's frame draw; PUT RENDERING/DRAWING CODE HERE
        
        if (Data.playing)
            Data.currentMap.render(gc, sbg, grphcs);
        gui.render(gc,grphcs);
    }
     
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
        //called on game's logical update loop; PUT GAME/LOGIC CODE HERE
        
        if (Data.playing) {
            Data.currentMap.update(gc, sbg, i);
            TriggerMgr.update(Data.currentMap, i);
        }
        gui.update(gc);
        if (gc.getInput().isKeyDown(Input.KEY_ESCAPE)) {
            Data.playing = false;
            Data.unloadMap();
            sbg.enterState(MapEditorState.ID);
        }
        script_console.updateConsole();
    }
     
    @Override
    public void enter (GameContainer gc, StateBasedGame sbg) throws SlickException {
        super.enter(gc, sbg);
        
        try {
            script_console = new ScriptConsole ();
            Thread t = new Thread ( () -> {
                script_console.setVisible(true);
            });
            t.start();
        } catch (Exception e) {
            Log.err(Log.MAP,"while trying to create script console",e);
            e.printStackTrace();
        }
    }
}
