/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.gui;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.GameContainer;

/**
 *
 * @author XyRoN (Emil SimoN)
 */
public class GuiController {
    public List<GuiElement> elements;
    
    public GuiController () {
        elements = new ArrayList<> ();
    }
    
    public void addElement (GuiElement e) {
        elements.add(e);
    }
    
    public void draw () {
        for (int i=0;i<elements.size();i++)
            if (elements.get(i).show)
                elements.get(i).draw();
    }
    
    public void update (GameContainer gc) {
        for (int i=0;i<elements.size();i++) {
            if (elements.get(i).show)
                elements.get(i).update(gc);
        }
    }
}
