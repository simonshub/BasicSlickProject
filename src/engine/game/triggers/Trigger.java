/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.triggers;

import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.game.maps.GameMap;
import engine.logger.Log;
import engine.utils.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * @author Emil Simon
 */

public class Trigger {
    public String name;
    public String code;
    public String description;
    public Set<String> events;
    public Set<String> loaded_triggers;
    public ScriptEngine engine;
    public boolean async;
    public boolean active_read;
    
    
    
    public Trigger () throws TriggerException {
        Log.log(Log.TRIG, "loading master trigger", true);
        name = "master";
        code = "";
        description = "Master trigger - this code is included automatically for every trigger.";
        async = false;
        active_read = false;
        events = new HashSet<> ();
        loaded_triggers = new HashSet<> ();
        engine = TriggerMgr.engine_mgr.getEngineByName(TriggerMgr.SCRIPT_ENGINE_NAME);
        readScript(new File (TriggerMgr.MASTER_SCRIPT_PATH));
    }
    public Trigger (String name, String path) throws TriggerException {
        Log.log(Log.TRIG, "loading trigger '"+name+"' at path '"+path+"'", true);
        this.name = name;
        code = "";
        description = "";
        async = false;
        active_read = false;
        events = new HashSet<> ();
        loaded_triggers = new HashSet<> ();
        engine = TriggerMgr.engine_mgr.getEngineByName(TriggerMgr.SCRIPT_ENGINE_NAME);
        loaded_triggers.add(TriggerMgr.master_trigger.name);
        readScript(path);
    }
    public Trigger (String name, File file) throws TriggerException {
        Log.log(Log.TRIG, "loading trigger '"+name+"' at path '"+FileUtils.getTriggerPath(file)+"'", true);
        this.name = name;
        code = "";
        description = "";
        async = false;
        active_read = false;
        events = new HashSet<> ();
        loaded_triggers = new HashSet<> ();
        engine = TriggerMgr.engine_mgr.getEngineByName(TriggerMgr.SCRIPT_ENGINE_NAME);
        loaded_triggers.add(TriggerMgr.master_trigger.name);
        readScript(file);
    }
    
    
    
    public final void readScript (String path) throws TriggerException {
        readScript(new File (path));
    }
    public final void readScript (File file) throws TriggerException {
        code = "";
        
        try {
            BufferedReader r = new BufferedReader (new FileReader (file));
            String line;
            while ((line=r.readLine())!=null) {
                if (line.trim().startsWith("@")) {
                    String[] words = line.substring(1).trim().split(" ");
                    switch (words.length) {
                        case 3:
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
                            }   break;
                        case 2:
                            switch (words[0].trim().toLowerCase()) {
                                case "event" :
                                    events.add(words[1].trim());
                                    Log.log(Log.TRIG, "added event '"+words[1].trim()+"' in trigger '"+name+"'");
                                    break;
                                case "load" :
                                    loaded_triggers.add(FileUtils.getNameWithoutExtension(words[1].trim()));
                                    Log.log(Log.TRIG, "loaded trigger '"+FileUtils.getNameWithoutExtension(words[1].trim())+"' for trigger '"+name+"'");
                                    break;
                                default :
                                    throw new TriggerException ();
                            }   break;
                        case 1:
                            switch (words[0].trim().toLowerCase()) {
                                case "async" :
                                    this.async = true;
                                    break;
                                case "dynamic" :
                                    this.active_read = true;
                                    break;
                                default :
                                    throw new TriggerException ();
                            }   break;
                        default:
                            if (words[0].trim().toLowerCase().equals("descr") && !active_read) {
                                this.description = line.replaceFirst("@","").replaceFirst("descr","").trim();
                                Log.log(Log.TRIG, "added description '"+this.description+"' for trigger '"+name+"'");
                            } else {
                                throw new TriggerException ();
                            }   break;
                    }
                } else {
                    code += line + "\n";
                }
            }
        } catch (FileNotFoundException ex) {
            Log.err(Log.TRIG,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - File not found!",ex);
        } catch (IOException ex) {
            Log.err(Log.TRIG,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - IOException",ex);
        } catch (TriggerException ex) {
            Log.err(Log.TRIG,"trigger '"+file.getPath().replace("\\", "/")+"' could not be parsed! - Script pre-eval line syntax error",ex);
        }
    }
    
    
    
    public void update (TriggerEvent[] event_list, GameMap context) {
        for (TriggerEvent event : event_list) {
            if (events.contains(event.eventName)) {
                String context_code = context.trigger_header + code;
                String event_code = event.getEventDefinition() + context_code;
                
                try {
                    if (this.async) {
                        Thread t = new Thread (() -> {
                            try {
                                engine.eval(event_code);
                            } catch (ScriptException ex) {
                                Log.err(Log.TRIG,"while trying to evaluate some code at async event '"+event+"' ...\nCODE:\n"+event_code,ex);
                            }
                        });
                        t.start();
                    } else {
                        engine.eval(event_code);
                    }
                } catch (ScriptException ex) {
                    Log.err(Log.TRIG,"while trying to evaluate some code at event '"+event+"' ...\nCODE:\n"+event_code,ex);
                }
                break;
            }
        }
    }
    
    
    
    public void run () {
        run (TriggerMgr.FORCED_EXECUTION_EVENT);
    }
    public void run (TriggerEvent event) {
        String eval_code = "";
        eval_code += TriggerMgr.master_trigger.code + "\n\n";
        eval_code += event.getEventDefinition();
        
        for (String trig_name : loaded_triggers) {
            if (!trig_name.equals(TriggerMgr.master_trigger.name)) {
                eval_code += ResMgr.getTrigger(trig_name).code + "\n\n";
            }
        }
        
        try {
            if (this.active_read)
                this.readScript(Consts.TRIGGER_DUMP_FOLDER+"/"+name+"."+Consts.TRIGGER_FILE_EXTENSION);
            
            eval_code += this.code + "\n\n";
            eval_code = eval_code.replace(TriggerMgr.EVENT_NAME_PLACEHOLDER, event.eventName);
            
            if (this.async) {
                final String async_code = eval_code;
                Thread t = new Thread (() -> {
                    try {
                        engine.eval(async_code);
                    } catch (ScriptException ex) {
                        Log.err(Log.TRIG,"while trying to evaluate some async code ...\nCODE:\n"+code,ex);
                    }
                });
                t.start();
            } else {
                engine.eval(eval_code);
            }
        } catch (ScriptException ex) {
            Log.err(Log.TRIG,"while trying to run some code ...\nCODE:\n"+eval_code,ex);
        } catch (TriggerException ex) {
            Log.err(Log.TRIG,"while trying to run some code ...\nCODE:\n"+eval_code,ex);
        }
    }
    
    
    
    @Override
    public String toString () {
        return "Trigger :\tname="+this.name+"\n"+
                "\t\tdescription="+this.description+"\n"+
                "\t\tcode;\n"+this.code+"\n";
    }
}
