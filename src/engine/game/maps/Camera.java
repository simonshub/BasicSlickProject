/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.maps;

import engine.game.entities.Entity;
import engine.utils.Location;
import engine.utils.Vector;
import engine.environment.Settings;

/**
 * @author Emil Simon
 */

public class Camera {
    public Entity follow_target;
    public Vector result_force;
    public Location location;
    public float zoom;
    
    private float force_x, force_y;
    
    
    public Camera () {
        follow_target = null;
        result_force = new Vector (0,0);
        location = new Location (0,0);
        zoom = 1.0f;
        force_x = 0;
        force_y = 0;
    }
     
    public Camera (int x, int y) {
        follow_target = null;
        result_force = new Vector (0,0);
        location = new Location (x,y);
        zoom = 1.0f;
        force_x = 0;
        force_y = 0;
    }
     
    public Camera (Entity e) {
        follow_target = e;
        result_force = new Vector (0,0);
        location = new Location (e.location.x, e.location.y);
        zoom = 1.0f;
        force_x = 0;
        force_y = 0;
    }
    
    
    
    public Location getLowerRight () {
        return new Location (location.x+Settings.screen_res_w, location.y+Settings.screen_res_h);
    }
    
    
     
    public void update () {
        if (!result_force.isNone() && follow_target==null) {
            force_x += result_force.x;
            force_y += result_force.y;
            location.x += (int) force_x;
            location.y += (int) force_y;
            force_x -= (int) force_x;
            force_y -= (int) force_y;
        } else if (follow_target!=null) {
            location.x = follow_target.location.x - Settings.screen_res_w/2;
            location.y = follow_target.location.y - Settings.screen_res_h/2;
        }
    }
}
