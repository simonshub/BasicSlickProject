/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui.elements;

import engine.gui.GuiController;
import engine.gui.GuiElement;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * @author Emil Simon
 */

public class SContainer extends GuiElement {
    List<GuiElement> elements;
    
    
    
    public SContainer () {
        super();
        
        elements = new ArrayList<> ();
    }
    
    
    @Override
    public void render (GameContainer gc, Graphics g) {
        super.render(gc, g);
        
        for (GuiElement el : elements) {
            el.render(gc, g);
        }
    }
    
    @Override
    public boolean update (GameContainer gc, GuiController parent) {
        boolean result = super.update(gc,parent)==true;
        
        for (GuiElement el : elements) {
            result = el.update(gc,parent)==true;
        }
        
        return result;
    }
}
