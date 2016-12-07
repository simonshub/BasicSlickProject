/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.game.triggers;

import java.util.HashMap;
import javax.script.ScriptEngine;

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
    
    
    
    public void addParam (String var_name, Object var_value) {
        params.put(var_name, var_value);
    }
    
    public void injectParams (ScriptEngine engine) {
        for (String key : params.keySet()) {
            engine.put(key, params.get(key));
        }
    }
    
//    public String getParamsCode () {
//        String code = "";
//        
//        for (String key : params.keySet()) {
//            code += "var "+key+" = "+params.get(key).toString();
//        }
//        
//        return code;
//    }
}
