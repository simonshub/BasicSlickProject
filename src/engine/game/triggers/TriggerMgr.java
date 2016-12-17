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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngineManager;

/**
 * @author Emil Simon
 */

public abstract class TriggerMgr {
    public static final String SCRIPT_ENGINE_NAME = "nashorn";
    public static final String FORCED_EXECUTION_EVENT = "_forced_run";
    public static final String MASTER_SCRIPT_PATH = Consts.TRIGGER_DUMP_FOLDER+"master.sts";
    public static final String EVENT_LIST_SCRIPT_PATH = Consts.TRIGGER_DUMP_FOLDER+"event_list.sts";
    
    public static final String EVENT_NAME_PLACEHOLDER = "_EVENT_NAME";
    
    public static ScriptEngineManager engine_mgr;
    public static Trigger master_trigger;
    public static Trigger event_list;
    public static List<TriggerEvent> fired_events;
    
    
    
    public static void init () {
        engine_mgr = new ScriptEngineManager ();
        fired_events = new ArrayList<> ();
        try {
            if (!new File (MASTER_SCRIPT_PATH).exists()) {
                BufferedWriter bw = new BufferedWriter (new FileWriter (new File (MASTER_SCRIPT_PATH)));
                String contents = "var entity = Java.type('engine.game.entities.Entity');" + "\n";
                contents += "var log = Java.type('engine.logger.Log');" + "\n";
                contents += "function console (text) {\n" +
                            "	log.console(text);\n" +
                            "}" + "\n";
                bw.write(contents);
                bw.flush();
                bw.close();
            }
            
            master_trigger = new Trigger ();
            master_trigger.run();
            
            if (!new File (EVENT_LIST_SCRIPT_PATH).exists()) {
                BufferedWriter bw = new BufferedWriter (new FileWriter (new File (EVENT_LIST_SCRIPT_PATH)));
                String contents = "";
                bw.write(contents);
                bw.flush();
                bw.close();
            }
            
            event_list = new Trigger ();
            event_list.run();
        } catch (IOException | TriggerException ex) {
            Log.err(Log.GENERAL, "could not create or read master trigger at '"+MASTER_SCRIPT_PATH+"' file!", ex);
        }
    }
    
    
    
    public static void update (GameMap context) {
        for (Trigger trig : ResMgr.trigger_lib.values()) {
            trig.update((TriggerEvent[]) fired_events.toArray(),context);
        }
        
        fired_events.clear();
    }
    
    public static void fire (TriggerEvent event) {
        if (!fired_events.contains(event)) fired_events.add(event);
    }
    
    public static void autoDetectTriggers () {
        Log.log(Log.TRIG, "auto-detecting triggers in folder '"+Consts.TRIGGER_DUMP_FOLDER+"' ...");
        List<File> files = FileUtils.getAllFiles(Consts.TRIGGER_DUMP_FOLDER);
        for (File f : files) {
            if ((FileUtils.getExtension(f.getPath().replace("\\","/")).equals(Consts.TRIGGER_FILE_EXTENSION)) &&
                    (!f.getPath().replace("\\","/").equals(TriggerMgr.MASTER_SCRIPT_PATH)) &&
                    (!f.getPath().replace("\\","/").equals(TriggerMgr.EVENT_LIST_SCRIPT_PATH))) {
                String trig_name = FileUtils.getTriggerName(f);
                try {
                    ResMgr.trigger_lib.put(trig_name, new Trigger (trig_name, f));
                    Log.log(Log.TRIG, "loaded trigger with name '"+trig_name+"' at path '"+FileUtils.getTriggerPath(f)+"'");
                } catch (TriggerException ex) {
                    Log.err(Log.TRIG, "could not load trigger with name '"+trig_name+"' at path '"+FileUtils.getTriggerPath(f)+"'", ex);
                }
            }
        }
    }
}
