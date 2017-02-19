/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui;

import engine.environment.ResMgr;
import engine.game.actors.AnimatedSprite;
import engine.game.triggers.TriggerEvent;
import engine.logger.Log;
import engine.utils.Location;
import engine.utils.Rect;
import engine.utils.StringUtils;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 * @author Emil Simon
 */

public abstract class GuiElement {
    public String name;
    public Rect rect;
    public boolean is_clicked;
    public boolean is_mouse_over;
    public boolean visible, enabled;
    
    public String on_mouse_up_trigger;
    public String on_mouse_down_trigger;
    public String on_hover_trigger;
    public String on_unhover_trigger;
    
    public String tooltip_text;
    public String hover_sprite_name;
    public String click_sprite_name;
    
    public String sprite_name;
    public Color filter;
    public Color overlay;
    public Color underlay;
    
    private Animation sprite;
    private Animation hover_sprite;
    private Animation click_sprite;
    
    
    
    public GuiElement () {
        name = "";
        rect = new Rect ();
        is_clicked = false;
        is_mouse_over = false;
        visible = true;
        enabled = true;
        
        on_mouse_up_trigger = "";
        on_mouse_down_trigger = "";
        on_hover_trigger = "";
        on_unhover_trigger = "";
        
        tooltip_text = "";
        hover_sprite_name = "";
        click_sprite_name = "";
        
        sprite_name = "";
        filter = new Color (1f,1f,1f);
        overlay = new Color (1f,1f,1f,0f);
        underlay = new Color (1f,1f,1f,0f);
        
        sprite = null;
        hover_sprite = null;
        click_sprite = null;
    }
    public GuiElement (String name) {
        this.name = name;
        rect = new Rect ();
        is_clicked = false;
        is_mouse_over = false;
        visible = true;
        enabled = true;
        
        on_mouse_up_trigger = "";
        on_mouse_down_trigger = "";
        on_hover_trigger = "";
        on_unhover_trigger = "";
        
        tooltip_text = "";
        hover_sprite_name = "";
        click_sprite_name = "";
        
        sprite_name = "";
        filter = new Color (1f,1f,1f);
        overlay = new Color (1f,1f,1f,0f);
        underlay = new Color (1f,1f,1f,0f);
        
        sprite = null;
        hover_sprite = null;
        click_sprite = null;
    }
    
    
    
    public void render (GameContainer gc, Graphics g) {
        if (!visible || rect.width==0 || rect.height==0)
            return;
        
        g.setColor(underlay);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        
        if (is_clicked && !click_sprite_name.isEmpty()) {
            if (click_sprite != null) {
                click_sprite.draw(rect.x, rect.y, rect.width, rect.height, filter);
            }
        } else if (is_mouse_over && !hover_sprite_name.isEmpty()) {
            if (hover_sprite != null) {
                hover_sprite.draw(rect.x, rect.y, rect.width, rect.height, filter);
            }
        } else {
            if (sprite != null) {
                sprite.draw(rect.x, rect.y, rect.width, rect.height, filter);
            }
        }
        
        g.setColor(overlay);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
    }
    
    public boolean renderTooltip (GameContainer gc, Graphics g) {
        if (is_mouse_over && !tooltip_text.isEmpty()) {
            //SHOW TOOLTIP
            if (ResMgr.hasFont(GuiController.TOOLTIP_FONT)) {
                g.setFont(ResMgr.getFont(GuiController.TOOLTIP_FONT));
                
                Rect tooltip_rect = new Rect (gc.getInput().getMouseX()+GuiController.TOOLTIP_X_OFFSET-GuiController.TOOLTIP_X_MARGIN,
                                              gc.getInput().getMouseY()+GuiController.TOOLTIP_Y_OFFSET-GuiController.TOOLTIP_Y_MARGIN,
                                              ResMgr.getFont(GuiController.TOOLTIP_FONT).getWidth(tooltip_text)+GuiController.TOOLTIP_X_MARGIN*2,
                                              ResMgr.getFont(GuiController.TOOLTIP_FONT).getHeight(tooltip_text)+GuiController.TOOLTIP_Y_MARGIN*2);
                
                if (ResMgr.hasSprite(GuiController.TOOLTIP_BACKGROUND_SPRITE)) {
                    ResMgr.getSprite(GuiController.TOOLTIP_BACKGROUND_SPRITE).draw(tooltip_rect.x, tooltip_rect.y, tooltip_rect.width, tooltip_rect.height);
                }
                
                g.setColor(Color.white);
                g.drawString(tooltip_text, tooltip_rect.x+GuiController.TOOLTIP_X_MARGIN, tooltip_rect.y+GuiController.TOOLTIP_Y_MARGIN);
                return true;
            }
        }
        return false;
    }
    
    public boolean update (GameContainer gc, GuiController parent) {
        if (!enabled)
            return false;
        
        if (rect.containsLocation(parent.mouse_position) && (parent.focusedElement.equals(this.name) || parent.focusedElement.isEmpty())) {
            if (!is_mouse_over && !on_hover_trigger.isEmpty() && ResMgr.hasTrigger(on_hover_trigger))
                ResMgr.getTrigger(on_hover_trigger).run(true, new TriggerEvent("gui_hover").addParam("element", this));
            
            is_mouse_over=true;
            
            if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
                if (is_clicked && !on_mouse_up_trigger.isEmpty() && ResMgr.hasTrigger(on_mouse_up_trigger))
                    ResMgr.getTrigger(on_mouse_up_trigger).run(true, new TriggerEvent("gui_mouseup").addParam("element", this));
                
                is_clicked = false;
            } else {
                if (!is_clicked && !on_mouse_down_trigger.isEmpty() && ResMgr.hasTrigger(on_mouse_down_trigger))
                    ResMgr.getTrigger(on_mouse_down_trigger).run(true, new TriggerEvent("gui_mousedown").addParam("element", this));
                
                is_clicked = true;
            }
            parent.focusedElement = this.name;
        } else {
            if (is_mouse_over && !on_unhover_trigger.isEmpty() && ResMgr.hasTrigger(on_unhover_trigger))
                ResMgr.getTrigger(on_unhover_trigger).run(true, new TriggerEvent("gui_unhover").addParam("element", this));
            
            is_mouse_over=false;
        }
        
        return is_clicked;
    }
    
    
    
    public abstract void fromWritten (String[] lines);
    
    public String getWritten () {
        String code = "";
        
        code += "\t"+"name:"+this.name+"\n";
        code += "\t"+"rect:"+this.rect.x+" "+this.rect.y+" "+this.rect.width+" "+this.rect.height+"\n";
        code += "\t"+"is_clicked:"+this.is_clicked+"\n";
        code += "\t"+"is_mouse_over:"+this.is_mouse_over+"\n";
        code += "\t"+"visible:"+this.visible+"\n";
        code += "\t"+"enabled:"+this.enabled+"\n";
        
        code += "\t"+"on_mouse_up_trigger:"+this.on_mouse_up_trigger+"\n";
        code += "\t"+"on_mouse_down_trigger:"+this.on_mouse_down_trigger+"\n";
        code += "\t"+"on_hover_trigger:"+this.on_hover_trigger+"\n";
        code += "\t"+"on_unhover_trigger:"+this.on_unhover_trigger+"\n";
        
        code += "\t"+"tooltip_text:"+this.tooltip_text+"\n";
        
        code += "\t"+"sprite_name:"+this.sprite_name+"\n";
        code += "\t"+"filter:"+this.filter.r+" "+this.filter.g+" "+this.filter.b+" "+this.filter.a+"\n";
        code += "\t"+"overlay:"+this.overlay.r+" "+this.overlay.g+" "+this.overlay.b+" "+this.overlay.a+"\n";
        code += "\t"+"underlay:"+this.underlay.r+" "+this.underlay.g+" "+this.underlay.b+" "+this.underlay.a+"\n";
        
        code += "\t"+"sprite:"+this.sprite_name+"\n";
        code += "\t"+"hover_sprite:"+this.hover_sprite_name+"\n";
        code += "\t"+"click_sprite:"+this.click_sprite_name+"\n";
        
        return code;
    }
    
    public boolean setAttribute (String line) {
        String[] word = StringUtils.removeEmpty(StringUtils.trimAll(line.split(":")));
        if (word.length != 2) {
            Log.err(Log.GENERAL, "bad gui attribute line; '"+line+"'");
            return false;
        }
        String[] args = StringUtils.removeEmpty(word[1].split(" "));
        
        switch (word[0]) {
            case "name" :
                if (args.length >= 1)
                    this.name = word[1].trim();
                break;
            case "rect" :
                if (args.length >= 4)
                    this.rect = new Rect (Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                break;
            case "visible" :
                if (args.length >= 1)
                    this.visible = Boolean.parseBoolean(args[0]);
                break;
            case "enabled" :
                if (args.length >= 1)
                    this.enabled = Boolean.parseBoolean(args[0]);
                break;
            case "on_mouse_up_trigger" :
                if (args.length >= 1)
                    this.on_mouse_up_trigger = args[0];
                break;
            case "on_mouse_down_trigger" :
                if (args.length >= 1)
                    this.on_mouse_down_trigger = args[0];
                break;
            case "on_hover_trigger" :
                if (args.length >= 1)
                    this.on_hover_trigger = args[0];
                break;
            case "on_unhover_trigger" :
                if (args.length >= 1)
                    this.on_unhover_trigger = args[0];
                break;
            case "tooltip_text" :
                if (args.length >= 1)
                    this.tooltip_text = args[0];
                break;
//            case "sprite_name" :
//                if (args.length >= 1)
//                    this.sprite_name = args[0];
//                break;
            case "filter" :
                if (args.length >= 4)
                    this.filter = new Color (Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
                break;
            case "overlay" :
                if (args.length >= 4)
                    this.overlay = new Color (Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
                break;
            case "underlay" :
                if (args.length >= 4)
                    this.underlay = new Color (Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
                break;
            case "sprite" :
                if (args.length >= 1)
                    this.setSprite(word[1].trim());
                break;
            case "hover_sprite" :
                if (args.length >= 1)
                    this.setHoverSprite(word[1].trim());
                break;
            case "click_sprite" :
                if (args.length >= 1)
                    this.setClickSprite(word[1].trim());
                break;
            default :
                Log.err(Log.GENERAL, "unknown gui attribute in line; '"+line+"'");
                return false;
        }
        return true;
    }
    
    
    
    public void hide () {
        this.visible = false;
    }
    public void show () {
        this.visible = true;
    }
    public void enable () {
        this.enabled = true;
    }
    public void disable () {
        this.enabled = false;
    }
    
    
    
    public GuiElement setRect (Rect rect) {
        this.rect = rect;
        return this;
    }
    public GuiElement setLocation (Location loc) {
        rect.x = loc.x;
        rect.y = loc.y;
        return this;
    }
    public GuiElement setSize (int width, int height) {
        rect.width = width;
        rect.height = height;
        return this;
    }
    
    public GuiElement setTooltip (String text) {
        this.tooltip_text = text;
        return this;
    }
    
    public GuiElement setGraphics (String sprite, String hover, String click) {
        if (ResMgr.hasAnimatedSprite(sprite))
            this.setSprite(sprite);
        if (ResMgr.hasAnimatedSprite(hover))
            this.setHoverSprite(hover);
        if (ResMgr.hasAnimatedSprite(click))
            this.setClickSprite(click);
        
        return this;
    }
    
    public GuiElement setSprite (String sprite) {
        if (!ResMgr.hasAnimatedSprite(sprite))
            return this;
        
        this.sprite_name = sprite;
        this.sprite = ResMgr.getAnimatedSprite(sprite).makeAnim();
        this.sprite.setAutoUpdate(true);
        return this;
    }
    public GuiElement setSprite (AnimatedSprite sprite) {
        this.sprite_name = sprite.name;
        this.sprite = sprite.makeAnim();
        return this;
    }
    
    public GuiElement setHoverSprite (String sprite) {
        if (!ResMgr.hasAnimatedSprite(sprite))
            return this;
        
        this.hover_sprite_name = sprite;
        this.hover_sprite = ResMgr.getAnimatedSprite(sprite).makeAnim();
        this.hover_sprite.setAutoUpdate(true);
        return this;
    }
    public GuiElement setHoverSprite (AnimatedSprite sprite) {
        this.hover_sprite_name = sprite.name;
        this.hover_sprite = sprite.makeAnim();
        return this;
    }
    
    public GuiElement setClickSprite (String sprite) {
        if (!ResMgr.hasAnimatedSprite(sprite))
            return this;
        
        this.click_sprite_name = sprite;
        this.click_sprite = ResMgr.getAnimatedSprite(sprite).makeAnim();
        this.click_sprite.setAutoUpdate(true);
        return this;
    }
    public GuiElement setClickSprite (AnimatedSprite sprite) {
        this.click_sprite_name = sprite.name;
        this.click_sprite = sprite.makeAnim();
        return this;
    }
    
    public GuiElement setFilter (float r, float g, float b, float a) {
        this.filter = new Color (r,g,b,a);
        return this;
    }
    public GuiElement setOverlay (float r, float g, float b, float a) {
        this.overlay = new Color (r,g,b,a);
        return this;
    }
    public GuiElement setUnderlay (float r, float g, float b, float a) {
        this.underlay = new Color (r,g,b,a);
        return this;
    }
    
    public GuiElement setOnHover (String trigger) {
        this.on_hover_trigger = trigger;
        return this;
    }
    public GuiElement setOnUnhover (String trigger) {
        this.on_unhover_trigger = trigger;
        return this;
    }
    public GuiElement setOnMouseUp (String trigger) {
        this.on_mouse_up_trigger = trigger;
        return this;
    }
    public GuiElement setOnMouseDown (String trigger) {
        this.on_mouse_down_trigger = trigger;
        return this;
    }
    
    public GuiElement setVisible (boolean visible) {
        this.visible = visible;
        return this;
    }
    public GuiElement setEnabled (boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
