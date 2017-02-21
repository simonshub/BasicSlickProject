/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.environment;

import engine.game.maps.GameMap;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author XyRoN
 */
public abstract class Data {
    
    public static boolean playing;
    public static GameMap currentMap;
    public static GameContainer gameContainer;
    public static StateBasedGame stateBasedGame;
    
    public static float gameSpeed=0.001f;
    
    
    
    public static void init (GameContainer gc, StateBasedGame sbg) {
        gameContainer = gc;
        stateBasedGame = sbg;
        playing = false;
        currentMap = null;
    }
    
    public static void loadMap (GameMap map) {
        playing = true;
        currentMap = map;
    }
    
    public static void unloadMap () {
        playing = false;
        currentMap = null;
    }
    
    public static void changeState (int ID) {
        stateBasedGame.enterState(ID);
    }
    
    public static void exitGame () {
        System.exit(0);
    }
    
}
