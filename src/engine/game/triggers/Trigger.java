/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.triggers;

import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.logger.Log;
import engine.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * @author Emil Simon
 */

public class Trigger {
    public Set<String> events;
    public String name;
    public String code;
    public ScriptEngine engine;
    public Bindings engine_bindings;
    
    
    
    public Trigger () {
        Log.log(Log.TRIG, "loading master trigger");
        name = "master";
        code = "";
        events = new HashSet<> ();
        engine = TriggerMgr.engine_mgr.getEngineByName(TriggerMgr.SCRIPT_ENGINE_NAME);
        engine_bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        loadScript(TriggerMgr.MASTER_SCRIPT_PATH, false);
    }
    public Trigger (String name, String path) {
        Log.log(Log.TRIG, "loading trigger '"+name+"' at path '"+path+"'");
        this.name = name;
        code = "var event = '"+TriggerMgr.EVENT_NAME_PLACEHOLDER+"';\n";
        events = new HashSet<> ();
        engine = TriggerMgr.engine_mgr.getEngineByName(TriggerMgr.SCRIPT_ENGINE_NAME);
        engine_bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        loadScript(path, true);
    }
    public Trigger (String name, File file) {
        Log.log(Log.TRIG, "loading trigger '"+name+"' at path '"+FileUtils.getTriggerPath(file)+"'");
        this.name = name;
        code = "var event = '"+TriggerMgr.EVENT_NAME_PLACEHOLDER+"';\n";
        events = new HashSet<> ();
        engine = TriggerMgr.engine_mgr.getEngineByName(TriggerMgr.SCRIPT_ENGINE_NAME);
        engine_bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        loadScript(file, true);
    }
    
    
    
    public final void loadScript (String path, boolean require_master) {
        Trigger.this.loadScript (new File (path), require_master);
    }
    public final void loadScript (File file, boolean require_master) {
        try {
            if (require_master){
                Log.log(Log.TRIG, "including master trigger for '"+this.name+"'");
                this.engine_bindings.putAll(TriggerMgr.master_trigger.engine_bindings);
                this.events.addAll(TriggerMgr.master_trigger.events);
                this.engine.eval(TriggerMgr.master_trigger.code);
            }
            
            if (ResMgr.trigger_lib.containsKey(name)) {
                Log.log(Log.TRIG, "reading existing trigger '"+name+"' for '"+this.name+"'");
                this.engine_bindings.putAll(ResMgr.trigger_lib.get(name).engine_bindings);
                this.events.addAll(ResMgr.trigger_lib.get(name).events);
                this.engine.eval(ResMgr.trigger_lib.get(name).code);
                return;
            }
            
            BufferedReader r = new BufferedReader (new FileReader (file));
            String line;
            while ((line=r.readLine())!=null) {
                if (line.startsWith("@")) {
                    String[] words = line.substring(1).trim().split(" ");
                    if (words.length == 3) {
                        switch (words[0].trim().toLowerCase()) {
                            case "bind" :
                                code += "var " + words[2].trim() + " = Java.type('"+words[1].trim()+"');" + "\n";
//                                engine_bindings.put(words[2].trim(), Class.forName(words[1].trim()));
                                Log.log(Log.TRIG, "bound '"+words[1].trim()+"' to '"+words[2].trim()+"' in trigger '"+name+"'");
                                break;
                            case "global" :
                                engine.put(words[1].trim(), words[2].trim());
                                Log.log(Log.TRIG, "bound '"+words[1].trim()+"' to '"+words[2].trim()+"' in trigger '"+name+"'");
                                break;
                            default :
                                throw new TriggerException ();
                        }
                    } else if (words.length == 2) {
                        switch (words[0].trim().toLowerCase()) {
                            case "event" :
                                events.add(words[1].trim());
                                Log.log(Log.TRIG, "added event '"+words[1].trim()+"' in trigger '"+name+"'");
                                break;
                            case "load" :
                                loadScript(Consts.TRIGGER_DUMP_FOLDER+words[1].trim(), false);
                                Log.log(Log.TRIG, "read trigger '"+FileUtils.getNameWithoutExtension(words[1].trim())+"' for trigger '"+name+"'");
                                break;
                            default :
                                throw new TriggerException ();
                        }
                    } else {
                        throw new TriggerException ();
                    }
                } else {
                    code += line + "\n";
                }
            }
        } catch (FileNotFoundException ex) {
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - File not found!");
        } catch (IOException ex) {
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - IOException");
        } catch (TriggerException ex) {
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - Script pre-eval line syntax error");
        } catch (ScriptException ex) {
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - ScriptException");
        } /*catch (ClassNotFoundException ex) {
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - ClassNotFoundException");
        }*/
    }
    
    
    
    public void update (TriggerEvent[] event_list) {
        for (TriggerEvent event : event_list) {
            if (events.contains(event.eventName)) {
                try {
                    event.injectParams(engine);
                    engine.eval(code);
                } catch (Exception ex) {
                    Log.log(Log.TRIG,Log.LogLevel.ERROR,"while trying to evaluate some code at event '"+event+"' ...\nCODE:\n"+code,true);
                    Log.log(Log.TRIG,Log.LogLevel.ERROR,"error message: "+ex.getMessage());
                }
                break;
            }
        }
    }
    
    
    
    public void run () {
        try {
            engine.eval(code.replace(TriggerMgr.EVENT_NAME_PLACEHOLDER, TriggerMgr.FORCED_EXECUTION_EVENT));
        } catch (Exception ex) {
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"while trying to run some code ...\nCODE:\n"+code,true);
            Log.log(Log.TRIG,Log.LogLevel.ERROR,"error message: "+ex.getMessage());
        }
    }
}
