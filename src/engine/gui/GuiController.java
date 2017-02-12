/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui;

import engine.utils.Location;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * @author Emil Simon
 */

public class GuiController {
    public static final String TOOLTIP_BACKGROUND_SPRITE = "tooltip";
    public static final String TOOLTIP_FONT = "tooltip";
    public static final int TOOLTIP_X_OFFSET = -16;
    public static final int TOOLTIP_Y_OFFSET = 16;
    public static final int TOOLTIP_X_MARGIN = 6;
    public static final int TOOLTIP_Y_MARGIN = 6;
    
    public boolean visible;
    public boolean lockFocus;
    public String focusedElement;
    public Location mouse_position;
    public List<GuiElement> guiElements;
    
    
    
    public GuiController () {
        guiElements = new ArrayList<> ();
        mouse_position = new Location ();
        visible = true;
        focusedElement = "";
        lockFocus = false;
    }
    
    
    
    public void addElement (GuiElement el) {
        guiElements.add(el);
    }
    
    public void render (GameContainer gc, Graphics g) {
        if (!visible)
            return;
        
        for (GuiElement el : guiElements)
            el.render(gc, g);
        
        for (int i=guiElements.size()-1;i>=0;i--) {
            if (guiElements.get(i).renderTooltip(gc,g)) break;
        }
    }
    
    public void update (GameContainer gc) {
        if (!visible)
            return;
        
        mouse_position.x = gc.getInput().getMouseX();
        mouse_position.y = gc.getInput().getMouseY();
        if (!lockFocus) focusedElement = "";
        
        for (GuiElement el : guiElements) {
            el.update(gc, this);
        }
    }
    
    public GuiElement[] getElementsByName (String name) {
        List<GuiElement> result = new ArrayList<> ();
        
        for (GuiElement el : guiElements) {
            if (el.name.equals(name))
                result.add(el);
        }
        
        return (GuiElement[]) result.toArray();
    }
    
    public GuiElement getFirstElementByName (String name) {
        for (GuiElement el : guiElements) {
            if (el.name.equals(name))
                return el;
        }
        return null;
    }
}
