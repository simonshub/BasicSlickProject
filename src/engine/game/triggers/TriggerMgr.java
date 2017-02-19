/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.triggers;

import engine.environment.Consts;
import engine.environment.Data;
import engine.environment.ResMgr;
import engine.environment.Settings;
import engine.game.maps.GameMap;
import engine.logger.Log;
import engine.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngineManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

/**
 * @author Emil Simon
 */

public abstract class TriggerMgr {
    public static final String SCRIPT_ENGINE_NAME = "nashorn";
    public static final String MASTER_SCRIPT_PATH = Consts.TRIGGER_DUMP_FOLDER+"master.sts";
    
    public static final String EVENT_NAME_PLACEHOLDER = "$event";
    public static final TriggerEvent FORCED_EXECUTION_EVENT = new TriggerEvent ("forced_run");
    public static final TriggerEvent GUI_HOVER_EVENT = new TriggerEvent ("gui_hover");
    public static final TriggerEvent GUI_UNHOVER_EVENT = new TriggerEvent ("gui_unhover");
    public static final TriggerEvent GUI_MOUSEDOWN_EVENT = new TriggerEvent ("gui_mousedown");
    public static final TriggerEvent GUI_MOUSEUP_EVENT = new TriggerEvent ("gui_mouseup");
    public static final TriggerEvent GAME_LOOP = new TriggerEvent ("update");
    public static final TriggerEvent BEFORE_GAME_LOOP = new TriggerEvent ("before_update");
    
    public static Trigger cmd;
    public static Trigger master_trigger;
    public static GameContainer gc;
    public static ScriptEngineManager engine_mgr;
    public static List<TriggerEvent> fired_events;
    
    public static String console;
    
    
    
    public static void init () {
        TriggerMgr.gc = Data.gameContainer;
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
            
            cmd = new Trigger ();
            cmd.name = "console";
            cmd.active_read = false;
            cmd.description = "";
            cmd.code = "";
            
            console = "";
        } catch (IOException | TriggerException ex) {
            Log.err(Log.GENERAL, "could not create or read master trigger at '"+MASTER_SCRIPT_PATH+"' file!", ex);
        }
    }
    
    
    
    public static void runCode (String code) {
        console("running code '"+code+"' ...");
        cmd.code = code;
        cmd.run();
        console("OK!");
    }
    
    public static void console (String text) {
        if (Settings.debug_triggers_console)
            Log.log(Log.TRIG, text);
        
        console += text+"\n";
    }
    
    public static void clearConsole () {
        console = "";
    }
    
    
    
    public static boolean isKeyDown (String key) {
        for (Field f : Input.class.getFields()) {
            if (f.getName().equalsIgnoreCase(key)) {
                try {
                    int key_code = f.getInt(Input.class);
                    return gc.getInput().isKeyDown(key_code);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Log.err(Log.TRIG, "cannot access members for Input", ex);
                }
            }
        }
        
        return false;
    }
    public static boolean isAnyKeyDown () {
        for (Field f : Input.class.getFields()) {
            try {
                if (f.getName().startsWith("KEY_") || f.getName().startsWith("MOUSE_"))
                    if (gc.getInput().isKeyDown(f.getInt(Input.class)))
                        return true;
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Log.err(Log.TRIG, "cannot access members for Input", ex);
            }
        }
    
        return false;
    }
    
    
    
    public static void update (GameMap context, int delta) {
        for (Trigger trig : ResMgr.trigger_lib.values()) {
            trig.runConditionally(context, delta, true, fired_events.toArray(new TriggerEvent [fired_events.size()]));
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
                    (!f.getPath().replace("\\","/").equals(TriggerMgr.MASTER_SCRIPT_PATH))) {
                String trig_name = FileUtils.getTriggerName(f);
                try {
                    ResMgr.trigger_lib.put(trig_name, new Trigger (trig_name, f));
                    Log.log(Log.TRIG, "loaded trigger with name '"+trig_name+"' at path '"+FileUtils.getTriggerPath(f)+"'", true);
                } catch (TriggerException ex) {
                    Log.err(Log.TRIG, "could not load trigger with name '"+trig_name+"' at path '"+FileUtils.getTriggerPath(f)+"'", ex);
                }
            }
        }
    }
}
