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
import engine.logger.Log;
import engine.utils.StringUtils;
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
        if (label!=null) label.render(gc, g);
    }
    
    @Override
    public boolean update (GameContainer gc, GuiController parent) {
        if (rect.containsLocation(parent.mouse_position) && gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !is_clicked && ResMgr.hasSound(sound_name))
            ResMgr.getSound(sound_name).play(1f, Settings.sfx_volume);
        
        return super.update(gc,parent);
    }
    
    
    
    public SButton setText (String text) {
        this.label = ((SLabel) new SLabel (this.name+"_label").setRect(rect)).setText(text);
        return this;
    }
    public SButton setOnClick (String sound, String trigger) {
        this.sound_name = sound;
        this.on_mouse_up_trigger = trigger;
        return this;
    }

    @Override
    public void fromWritten(String[] lines) {
        for (String line : lines) {
            this.setAttribute(line);
        }
    }

    @Override
    public String getWritten() {
        String code = super.getWritten();
        
        code += "\t"+"label:"+this.label.text+"\n";
        code += "\t"+"on_click:"+this.sound_name+" "+this.on_mouse_up_trigger+"\n";
        
        return code;
    }
    
    @Override
    public boolean setAttribute (String line) {
        String[] word = StringUtils.removeEmpty(StringUtils.trimAll(line.split(":")));
        if (word.length != 2) {
            Log.err(Log.GENERAL, "bad gui attribute line; '"+line+"'");
            return false;
        }
        String[] args = StringUtils.removeEmpty(word[1].split(" "));
        
        switch (word[0]) {
            case "label" :
                this.setText(word[1].trim());
                break;
            case "on_click" :
                this.setOnClick(args[0],args[1]);
                break;
        }
        
        return super.setAttribute(line);
    }
}
