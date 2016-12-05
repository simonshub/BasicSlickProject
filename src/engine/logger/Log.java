/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.logger;

import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.environment.Settings;
import engine.utils.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Emil Simon
 */

public abstract class Log {
    public enum LogLevel { ERROR,CONSOLE,FATAL };
    
    public static LogType ENTITY;
    public static LogType ACTOR;
    public static LogType MAP;
    public static LogType TRIG;
    public static LogType GENERAL;
    
    public static final String DIVIDER = "----------\n";
    
    
    
    public static void log (LogType log, String message) {
        log (log, LogLevel.CONSOLE, message, false, false);
    }
    public static void log (LogType log, LogLevel lvl, String message) {
        log (log, lvl, message, false, false);
    }
    public static void log (LogType log, String message, boolean divider) {
        log (log, LogLevel.CONSOLE, message, divider, false);
    }
    public static void log (LogType log, LogLevel lvl, String message, boolean divider) {
        log (log, lvl, message, divider, false);
    }
    public static void log (LogType log, LogLevel lvl, String message, boolean divider, boolean silent) {
        if ((log == ENTITY) && (!Settings.debug_entities)) {
            return;
        } else if ((log == ACTOR) && (!Settings.debug_actors)) {
            return;
        } else if ((log == MAP) && (!Settings.debug_maps)) {
            return;
        } else if ((log == TRIG) && (!Settings.debug_triggers)) {
            return;
        }
        
        String tolog = "";
        
        tolog += log.prefix + ((lvl==LogLevel.ERROR) ? "ERROR : " : "") + ((lvl==LogLevel.FATAL) ? "FATAL : " : "");
        tolog += StringUtils.capitalizeFirstChar(message);
        
        writeToLog (log.log_file, tolog, divider, silent);
    }
    
    
    
    public static void silentLog (LogType log, String message) {
        log (log, LogLevel.CONSOLE, message, false, true);
    }
    public static void console (String message) {
        log(Log.TRIG, message, false);
    }
    
    
    
    public static void endLog () {
        silentLog(Log.ACTOR, DIVIDER);
        silentLog(Log.ENTITY, DIVIDER);
        silentLog(Log.MAP, DIVIDER);
        silentLog(Log.TRIG, DIVIDER);
        silentLog(Log.GENERAL, DIVIDER);
    }
    
    
    
    public static void init () {
        ENTITY = new LogType ("ENT : ",Consts.log_file_path_ent);
        ACTOR = new LogType ("ACT : ",Consts.log_file_path_act);
        MAP = new LogType ("MAP : ",Consts.log_file_path_map);
        TRIG = new LogType ("TRIG : ",Consts.log_file_path_script);
        GENERAL = new LogType ("LOG : ",Consts.log_file_path_general);
    }
    
    
    
    public static void createLog (String file_path) {
        try {
            File f = new File(file_path);
            if (!f.exists()) {
                PrintWriter log = new PrintWriter (file_path, "UTF-8");
                log.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ResMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void writeToLog (String log_file, String line, boolean divider, boolean silent) {
        try {
            Writer log = new BufferedWriter (new FileWriter (log_file, true));
            log.append((divider ? "\n"+DIVIDER : "") +
                       getTimestamp() + "    " + line + "\n" +
                       (divider ? DIVIDER : ""));
            if (!silent)
                System.out.println(line);
            log.close();
        } catch (IOException ex) {
            Logger.getLogger(ResMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static String getTimestamp () {
        return Consts.log_timestamp_form.format(new Date());
    }
}
