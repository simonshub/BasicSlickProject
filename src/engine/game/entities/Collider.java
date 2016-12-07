/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.logger.Log;

/**
 * @author Emil Simon
 */

public class Collider {
    public enum ColliderState { BOX, RADIAL, NONE };
    public ColliderState state;
    
    public int radius;
    public int box_width, box_height;
    public int height_layer;
    public boolean floating;
     
    public Collider () {
        state = ColliderState.NONE;
        radius = 0;
        box_width = 0;
        box_height = 0;
        height_layer = 1;
        floating = true;
    }
     
    public boolean setState (String state) {
        state = state.toLowerCase().trim();

        if (state.equals("rad") || state.equals("radial")) {
            this.state = ColliderState.RADIAL;
        } else if (state.equals("box")) {
            this.state = ColliderState.BOX;
        } else if (state.equals("none") || state.equals("null")) {
            this.state = ColliderState.NONE;
        } else {
            Log.err(Log.ENTITY,"unknown collider state '"+state+"'",null);
            return false;
        }

        return true;
    }
}
