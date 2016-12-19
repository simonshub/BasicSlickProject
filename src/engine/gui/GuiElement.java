/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.gui;

import engine.utils.Callable;
import engine.environment.ResMgr;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author XyRoN (Emil SimoN)
 */
public class GuiElement {
    public int x, y;
    public String grfx;
    public Color grfx_color = Color.white;
    
    public boolean show = true;
    public boolean hovered = false;
    public boolean btn_down = false;
    
    public String text = "";
    public String text_font;
    public Color text_color = Color.white;
    public int text_x, text_y;
    
    public Callable onMouseDown = null;
    public Callable onMouseUp = null;
    public Callable onHover = null;
    public Callable onUnhover = null;
    
    public void draw () {
        if (grfx!=null)
            ResMgr.getSprite(grfx).draw(x, y, grfx_color);
        
        if (!text.equals("") && (ResMgr.font_lib.get(text_font) != null))
            ResMgr.font_lib.get(text_font).drawString(text_x, text_y, text, text_color);
    }
    
    public void update (GameContainer gc) {
        int mouseX = gc.getInput().getMouseX();
        int mouseY = gc.getInput().getMouseY();
        SpriteSheet s = ResMgr.getSprite(grfx);
        
        if (!((ResMgr.font_lib.get(text_font) == null) || text.isEmpty() || (text == null))) {
            text_x = x + s.getWidth()/2 - ResMgr.font_lib.get(text_font).getWidth(text)/2;
            text_y = y + s.getHeight()/2 - ResMgr.font_lib.get(text_font).getHeight(text)/2;
        }
        
        if (((mouseX > x) && (mouseX < x + s.getWidth())) && ((mouseY > y) && (mouseY < y + s.getHeight()))) {
            if (!hovered) {
                hovered = true;
                onHover.call();
            }
            
            if (gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !btn_down) {
                btn_down = true;
                onMouseDown.call();
            } else if (!gc.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && btn_down) {
                btn_down = false;
                onMouseUp.call();
            }
        } else {
            if (hovered) {
                hovered = false;
                btn_down = false;
                onUnhover.call();
            }
        }
    }
}
