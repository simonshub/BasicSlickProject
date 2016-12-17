/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package editors.toolbars.trigger;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.PlainDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author Emil Simon
 */

public abstract class JavaScriptSyntaxMgr {
    
    public static void syntaxHighlight (JTextPane pane) {
        StyledDocument doc = pane.getStyledDocument();
        doc.putProperty(PlainDocument.tabSizeAttribute, 4);
        
        Style entire = doc.addStyle("entire", null);
        entire.addAttribute(StyleConstants.FontFamily, "Lucida Console");
        entire.addAttribute(StyleConstants.Bold, false);
        entire.addAttribute(StyleConstants.Italic, false);
        entire.addAttribute(StyleConstants.Foreground, Color.black);
        entire.addAttribute(StyleConstants.Background, Color.white);
        
        Style var = doc.addStyle("var", null);
        var.addAttribute(StyleConstants.FontFamily, "Lucida Console");
        var.addAttribute(StyleConstants.Bold, true);
        var.addAttribute(StyleConstants.Italic, false);
        var.addAttribute(StyleConstants.Foreground, new Color (0f,0.5f,0.3f));
        var.addAttribute(StyleConstants.Background, Color.white);
        
        Style function = doc.addStyle("function", null);
        function.addAttribute(StyleConstants.FontFamily, "Lucida Console");
        function.addAttribute(StyleConstants.Bold, true);
        function.addAttribute(StyleConstants.Italic, false);
        function.addAttribute(StyleConstants.Foreground, new Color (0f,0f,0.8f));
        function.addAttribute(StyleConstants.Background, Color.white);
        
        Style ifelse = doc.addStyle("ifelse", null);
        ifelse.addAttribute(StyleConstants.FontFamily, "Lucida Console");
        ifelse.addAttribute(StyleConstants.Bold, true);
        ifelse.addAttribute(StyleConstants.Italic, false);
        ifelse.addAttribute(StyleConstants.Foreground, new Color (0f,0f,1f));
        ifelse.addAttribute(StyleConstants.Background, Color.white);
        
        Style preeval = doc.addStyle("preeval", null);
        preeval.addAttribute(StyleConstants.FontFamily, "Lucida Console");
        preeval.addAttribute(StyleConstants.Bold, false);
        preeval.addAttribute(StyleConstants.Italic, false);
        preeval.addAttribute(StyleConstants.Foreground, new Color (0f,0.2f,0f));
        preeval.addAttribute(StyleConstants.Background, Color.gray);
        
        
    }
    
}
