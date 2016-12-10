/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.environment.ResMgr;
import engine.game.actors.AnimatedSprite;
import engine.game.maps.Camera;
import engine.logger.Log;
import engine.utils.Location;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

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
    
    public void render (Graphics g, Location loc, Camera c, Color filter) {
        render (g,loc.x,loc.y,c,filter);
    }
    public void render (Graphics g, int x, int y, Camera c, Color filter) {
        g.setColor(filter);
        if (state == Collider.ColliderState.BOX) {
            g.drawRect(x - c.location.x - box_width/2,
                       y - c.location.y - box_height/2,
                       box_width, box_height);
            g.fillRect(x - c.location.x - box_width/2,
                       y - c.location.y - box_height/2,
                       box_width, box_height);
        } else if (state == Collider.ColliderState.RADIAL) {
            g.drawOval(x - c.location.x - radius,
                       y - c.location.y - radius,
                       radius*2, radius*2);
            g.fillOval(x - c.location.x - radius,
                       y - c.location.y - radius,
                       radius*2, radius*2);
        }
    }
    public void renderExplicit (Graphics g, int x, int y, Color filter) {
        g.setColor(filter);
        if (state == Collider.ColliderState.BOX) {
            g.drawRect(x - box_width/2,
                       y - box_height/2,
                       box_width, box_height);
            g.fillRect(x - box_width/2,
                       y - box_height/2,
                       box_width, box_height);
        } else if (state == Collider.ColliderState.RADIAL) {
            g.drawOval(x - radius,
                       y - radius,
                       radius*2, radius*2);
            g.fillOval(x - radius,
                       y - radius,
                       radius*2, radius*2);
        }
    }
}
