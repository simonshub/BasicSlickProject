/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.utils;

/**
 * @author Emil Simon
 */

public class Rect {
    public int x,y;
    public int width,height;
    
    
    
    public Rect () {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }
    public Rect (Location loc, int width, int height) {
        this.x = loc.x;
        this.y = loc.y;
        this.width = width;
        this.height = height;
    }
    public Rect (Location upper_left, Location lower_right) {
        this.x = Math.min(upper_left.x, lower_right.x);
        this.y = Math.min(upper_left.y, lower_right.y);
        this.width = Math.max(upper_left.x, lower_right.x)-this.x;
        this.height = Math.max(upper_left.y, lower_right.y)-this.y;
    }
    public Rect (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    
    
    public boolean containsLocation (Location loc) {
        return (loc.x >= this.x) &&
               (loc.y >= this.y) &&
               (loc.x <= this.x+this.width) &&
               (loc.y <= this.y+this.height);
    }
    public Location upperLeft () {
        return new Location (x,y);
    }
    public Location upperRight () {
        return new Location (x+width,y);
    }
    public Location lowerLeft () {
        return new Location (x,y+height);
    }
    public Location lowerRight () {
        return new Location (x+width,y+height);
    }
}
