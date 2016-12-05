/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.project;

import engine.environment.Settings;
import engine.environment.ResMgr;
import editors.gamestates.*;
import engine.gamestates.*;
import engine.logger.Log;
import java.io.File;
import java.util.Scanner;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author XyRoN (Emil SimoN)
 */
public class ProjectMain extends StateBasedGame {
    public static int WIDTH=800;
    public static int HEIGHT=600;
    
    public static void main(String[] args) {
        System.out.println ("Starting ...");
        File file = new File ("natives");
        if (file.exists()) {
            switch(LWJGLUtil.getPlatform()) {
                case LWJGLUtil.PLATFORM_WINDOWS:
                    file = new File("native/windows/");
                    break;
                case LWJGLUtil.PLATFORM_LINUX:
                    file = new File("native/linux/");
                    break;
                case LWJGLUtil.PLATFORM_MACOSX:
                    file = new File("native/macosx/");
                    break;
                default:
                    file = new File("native/windows/");
                    break;
            }
            
            System.out.println("LWJGL Natives : '"+file.getAbsolutePath()+"'");
            System.setProperty("org.lwjgl.librarypath",file.getAbsolutePath());
        }
        
        Runtime.getRuntime().addShutdownHook (new ResMgr.onExit());
        
        try {
            Log.init();
            Settings.readSettings();
            
            WIDTH = Settings.screen_res_w;
            HEIGHT = Settings.screen_res_h;
            
            AppGameContainer agc = new AppGameContainer (new ProjectMain (Settings.title));
            agc.setDisplayMode (WIDTH, HEIGHT, false);
            
            agc.start();
        } catch (SlickException e) {
            System.out.println("Couldn't start the game.");
            System.exit(-1);
        }
    }

    public ProjectMain(String name) {
        super(name);
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        gc.setMaximumLogicUpdateInterval(60);
        gc.setTargetFrameRate(60);
        gc.setAlwaysRender(true);
        
        if (Settings.devmode) {
            Scanner scan = new Scanner (System.in);
            String input;
            
//            System.out.println ("[DEVMODE] Enter additional start options:");
//            System.out.println ("\tForce default settings? ");
//            input = scan.nextLine().toLowerCase();
//            Settings.force_default_settings = (input.equals("yes") || input.equals("y") ||
//                                             input.equals("true") || input.equals("t") ||
//                                             input.equals("1"));
//            System.out.println ("\tForce default settings? ");
//            input = scan.nextLine().toLowerCase();
//            ResMgr.force_default_settings = (input.equals("yes") || input.equals("y") ||
//                                             input.equals("true") || input.equals("t") ||
//                                             input.equals("1"));
        }
        
        ResMgr.init();
        
        gc.setVSync(Settings.grfx_vsync);
        
        this.addState (new CombatState ());
        this.addState (new MenuState ());
        this.addState (new WorldState ());
        
        if (Settings.devmode) {
            this.addState (new ActorEditorState ());
            this.addState (new EntityEditorState ());
            this.addState (new MapEditorState ());
        }
        
        this.enterState(MenuState.ID);
    }
    
}
