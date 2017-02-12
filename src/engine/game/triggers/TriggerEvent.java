/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.game.triggers;

import com.google.gson.Gson;
import java.util.HashMap;

/**
 *
 * @author SimonBTB
 */
public class TriggerEvent {
    public String eventName;
    public HashMap<String,Object> params;
    
    
    
    public TriggerEvent (String name) {
        eventName = name;
        params = new HashMap<> ();
    }
    
    
    
    public TriggerEvent addParam (String var_name, Object var_value) {
        params.put(var_name, var_value);
        return this;
    }
    public TriggerEvent param (String var_name, Object var_value) {
        params.put(var_name, var_value);
        return this;
    }
    
    
    
    public String getEventDefinition () {
        String code = "var event = {" + "\n";
        code += "\tname: '"+eventName+"',\n";
        Gson gson = new Gson ();
        
        for (String key : params.keySet()) {
            code += "\t"+key+": "+gson.toJson(params.get(key))+",\n";
        }
        
        code += "}\n\n\n";
        return code;
    }
}
