/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.actors;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 * @author Emil Simon
 */
public class AnimatedSprite {
    public String name;
    public SpriteSheet sheet;
    public int dimX, dimY;
     
    public AnimatedSprite (String name, String spriteSheetPath, int dimX, int dimY) throws SlickException {
        this.name = name;
        this.sheet = new SpriteSheet (spriteSheetPath, dimX, dimY);
        this.dimX = dimX;
        this.dimY = dimY;
    }
}
