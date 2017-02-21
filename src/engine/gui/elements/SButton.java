/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui.elements;

import engine.environment.ResMgr;
import engine.environment.Settings;
import engine.game.triggers.TriggerEvent;
import engine.gui.GuiController;
import engine.gui.GuiElement;
import engine.logger.Log;
import engine.utils.StringUtils;
import java.util.HashSet;
import java.util.Set;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * @author Emil Simon
 */

public class SButton extends GuiElement {
    
    public SLabel label;
    public String sound_name;
    public Set<Integer> hotkeys;
    
    
    
    public SButton (String name) {
        super(name);
        hotkeys = new HashSet<> ();
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
        
        int hk = 0;
        for (int key : hotkeys) {
            if (gc.getInput().isKeyDown(key))
                hk++;
        }
        
        if (hotkeys.size() == hk && hk > 0) {
            if (!is_clicked && !on_mouse_down_trigger.isEmpty() && ResMgr.hasTrigger(on_mouse_down_trigger))
                ResMgr.getTrigger(on_mouse_down_trigger).run(true, new TriggerEvent("gui_mousedown").addParam("element", this));

            is_clicked = true;
            parent.focusedElement = this.name;
            
            return is_clicked;
        }
        
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
    public SButton setHotkey (int... keycode) {
        for (int i : keycode)
            hotkeys.add(i);
            
        return this;
    }

    @Override
    public String getWritten() {
        String code = super.getWritten();
        
        code += "\t"+"label:"+this.label.text+"\n";
        code += "\t"+"on_click:"+this.sound_name+" "+this.on_mouse_up_trigger+"\n";
        code += "\t"+"hotkeys:";
        for (int key : hotkeys)   code += " " + key;
        code += "\n";
        
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
            case "hotkeys" :
                int[] keycodes = new int [args.length];
                for (int i=0;i<args.length;i++) {
                    keycodes[i] = Integer.parseInt(args[i].trim());
                }
                this.setHotkey(keycodes);
                break;
        }
        
        return super.setAttribute(line);
    }
}
