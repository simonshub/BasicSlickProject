/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.actors;

/**
 * @author Emil Simon
 */

public class AnimFrame {
    public String animation;
    public int dur, x, y;
    
    public AnimFrame (String anim, int dur, int x, int y) {
        this.animation = anim;
        this.dur = dur;
        this.x = x;
        this.y = y;
    }
    
    public AnimFrame (AnimFrame parent) {
        this.animation = parent.animation;
        this.dur = parent.dur;
        this.x = parent.x;
        this.y = parent.y;
    }
}
