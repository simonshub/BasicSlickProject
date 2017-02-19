/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.gui.elements;

import engine.gui.GuiController;
import engine.gui.GuiElement;
import engine.logger.Log;
import engine.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * @author Emil Simon
 */

public class SContainer extends GuiElement {
    public List<GuiElement> elements;
    
    protected boolean define_element;
    protected ArrayList<String> element_lines;
    
    
    
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
    
    
    
    @Override
    public void fromWritten(String[] lines) {
        for (String line : lines) {
            this.setAttribute(line);
        }
    }
    
    @Override
    public String getWritten() {
        String code = super.getWritten();
        
        for (GuiElement el : elements) {
            code += "element"+"\n";
            code += el.getWritten();
            code += "element_end"+"\n\n";
        }
        
        return code;
    }
    
    @Override
    public boolean setAttribute (String line) {
        String[] word = StringUtils.removeEmpty(StringUtils.trimAll(line.split(":")));
        
        if (define_element) {
            element_lines.add(line);
            return true;
        }
        
        switch (word[0]) {
            case "element" :
                element_lines = new ArrayList<> ();
                define_element = true;
                return true;
            case "end_element" :
                define_element = false;
//                elements.add(new GuiElement ());
                return true;
        }
        
        return super.setAttribute(line);
    }
}
