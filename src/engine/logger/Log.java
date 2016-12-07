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
    
    
    
    public static void err (LogType log, String message, Exception ex) {
        log (log, LogLevel.ERROR, message, false, false);
        if (ex!=null)
            log (log, LogLevel.ERROR, "Exception: "+ex.getClass().getName()+" : "+ex.getMessage(), false, false);
    }
    
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
        if (((log == ENTITY) && (!Settings.debug_entities)) ||
                ((log == ACTOR) && (!Settings.debug_actors)) ||
                ((log == MAP) && (!Settings.debug_maps)) ||
                ((log == TRIG) && (!Settings.debug_triggers))) {
            return;
        }
        
        String tolog = "";
        
        tolog += log.prefix + ((lvl==LogLevel.ERROR) ? "ERROR : " : "") + ((lvl==LogLevel.FATAL) ? "FATAL : " : "");
        tolog += StringUtils.capitalizeFirstChar(message);
        
        writeToLog (log.log_file, tolog, divider, silent);
        
        if (lvl==LogLevel.FATAL) System.exit(1);
    }
    
    
    
    public static void silentLog (LogType log, String message) {
        log (log, LogLevel.CONSOLE, message, false, true);
    }
    public static void console (String message) {
        log(Log.TRIG, LogLevel.CONSOLE, message, false, false);
    }
    public static void error (String message) {
        log(Log.TRIG, Log.LogLevel.ERROR, message, false, false);
    }
    
    
    
    public static void endLog () {
        silentLog(Log.ACTOR, DIVIDER);
        silentLog(Log.ENTITY, DIVIDER);
        silentLog(Log.MAP, DIVIDER);
        silentLog(Log.TRIG, DIVIDER);
        silentLog(Log.GENERAL, DIVIDER);
    }
    
    
    
    public static void init () {
        ENTITY = new LogType ("ENT : ",Consts.LOG_FILE_PATH_ENT);
        ACTOR = new LogType ("ACT : ",Consts.LOG_FILE_PATH_ACT);
        MAP = new LogType ("MAP : ",Consts.LOG_FILE_PATH_MAP);
        TRIG = new LogType ("TRIG : ",Consts.LOG_FILE_PATH_TRIG);
        GENERAL = new LogType ("LOG : ",Consts.LOG_FILE_PATH_GENERAL);
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
        return Consts.LOG_TIMESTAMP_FORM.format(new Date());
    }
}
