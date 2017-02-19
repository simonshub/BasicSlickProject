/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui.elements;

import engine.environment.ResMgr;
import engine.gui.GuiController;
import engine.gui.GuiElement;
import engine.logger.Log;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * @author Emil Simon
 */

public class SLabel extends GuiElement {
    public enum HTextAlign { LEFT, CENTER, RIGHT };
    public enum VTextAlign { UP, CENTER, DOWN };
    
    public HTextAlign h_text_align=HTextAlign.CENTER;
    public VTextAlign v_text_align=VTextAlign.CENTER;
    
    String text;
    String font;
    
    
    
    public SLabel (String name) {
        this.name = name;
        this.font = "normal_font";
    }
    
    public SLabel setText (String text) {
        this.text = text;
        return this;
    }
    public SLabel setFont (String font) {
        this.font = font;
        return this;
    }
    public SLabel setTextAlignment (String horizontal, String vertical) {
        try {
            h_text_align = HTextAlign.valueOf(horizontal.toUpperCase());
            v_text_align = VTextAlign.valueOf(vertical.toUpperCase());
        } catch (Exception e) { Log.err(Log.GENERAL, "while setting element alignment for '"+name+"' to '"+horizontal+"' and '"+vertical+"'", e); }
        return this;
    }
    
    
    
    @Override
    public void render (GameContainer gc, Graphics g) {
        super.render(gc, g);
        
        Font f = ResMgr.hasFont(font) ? ResMgr.getFont(font) : null;
        if (f==null) return;
        
        g.setFont(f);
        g.setColor(filter);
        int width = f.getWidth(text);
        int height = f.getHeight(text);
        g.drawString(text, rect.centerX()-width/2, rect.centerY()-height/2);
    }
    
    @Override
    public boolean update (GameContainer gc, GuiController parent) {
        return super.update(gc,parent);
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
        
        // some code
        
        return code;
    }
    
    @Override
    public boolean setAttribute (String line) {
        return true;
    }
}
