/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui.elements;

import engine.environment.ResMgr;
import engine.environment.Settings;
import engine.gui.GuiController;
import engine.gui.GuiElement;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * @author Emil Simon
 */

public class SButton extends GuiElement {
    public SLabel label;
    public String sound_name;
    
    
    
    public SButton (String name) {
        super(name);
        
        sound_name = "";
    }
    
    
    @Override
    public void render (GameContainer gc, Graphics g) {
        super.render(gc, g);
    }
    
    @Override
    public void update (GameContainer gc, GuiController parent) {
        if (rect.containsLocation(parent.mouse_position) && gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !is_clicked && ResMgr.hasSound(sound_name))
            ResMgr.getSound(sound_name).play(1f, Settings.sfx_volume);
        
        super.update(gc,parent);
    }
    
    
    
    public SButton setText (String text) {
        this.label = (SLabel) new SLabel (this.name+"_label").setText(text).setRect(rect);
        return this;
    }
    public SButton setOnClick (String sound, String trigger) {
        this.sound_name = sound;
        this.on_mouse_up_trigger = trigger;
        return this;
    }
}
