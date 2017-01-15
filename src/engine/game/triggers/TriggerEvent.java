/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.game.triggers;

import java.util.HashMap;

/**
 *
 * @author SimonBTB
 */
public class TriggerEvent {
    public String eventName;
    public HashMap<String,String> params;
    
    
    
    public TriggerEvent (String name) {
        eventName = name;
        params = new HashMap<> ();
    }
    
    
    
    public TriggerEvent addParam (String var_name, String var_value) {
        params.put(var_name, var_value);
        return this;
    }
    public TriggerEvent param (String var_name, String var_value) {
        params.put(var_name, var_value);
        return this;
    }
    
    
    
    public String getEventDefinition () {
        String code = "var event = {" + "\n";
        code += "\tname: '"+eventName+"',\n";
        
        for (String key : params.keySet()) {
            code += "\t"+key+": '"+params.get(key)+"',\n";
        }
        
        code += "}\n\n\n";
        return code;
    }
}
