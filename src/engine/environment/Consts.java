/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.environment;

import engine.game.triggers.TriggerMgr;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Emil Simon
 */

public abstract class Consts {
    public static final SimpleDateFormat LOG_TIMESTAMP_FORM = new SimpleDateFormat ("HH:mm:ss");
    public static final SimpleDateFormat LOG_DATE_FORM = new SimpleDateFormat ("dd_MM_");
    
    public static final String LOG_FILE_PATH_GENERAL = "logs/" + LOG_DATE_FORM.format(new Date ()) + "_log.stf";
    public static final String LOG_FILE_PATH_ENT = "logs/" + LOG_DATE_FORM.format(new Date ()) + "_entity_log.stf";
    public static final String LOG_FILE_PATH_ACT = "logs/" + LOG_DATE_FORM.format(new Date ()) + "_actor_log.stf";
    public static final String LOG_FILE_PATH_MAP = "logs/" + LOG_DATE_FORM.format(new Date ()) + "_map_log.stf";
    public static final String LOG_FILE_PATH_TRIG = "logs/" + LOG_DATE_FORM.format(new Date ()) + "_trig_log.stf";
    
    public static final String SETTINGS_FILE_PATH = "res/data/settings.stf";
    public static final String LOADER_FILE_PATH = "res/data/loader.stf";
    public static final String ACTORS_FILE_PATH = "res/data/actors/actors.stf";
    public static final String ENTITIES_FILE_PATH = "res/data/entities/entities.stf";
    public static final String ACTOR_DUMP_FOLDER = "res/data/actors/";
    public static final String ENTITY_DUMP_FOLDER = "res/data/entities/";
    public static final String TRIGGER_DUMP_FOLDER = "res/data/triggers/";
    public static final String MAP_DUMP_FOLDER = "res/data/maps/";
     
    public static final int TILESET_FRAME_WIDTH = 32;
    public static final int TILESET_FRAME_HEIGHT = 32;
    
    public static final int ENTITY_OFFSCREEN_DRAW_MARGIN = 128;
    
    public static final String ACTOR_FILE_EXTENSION = "act";
    public static final String ENTITY_FILE_EXTENSION = "ent";
    public static final String TRIGGER_FILE_EXTENSION = "sts";
    public static final String MAP_FILE_EXTENSION = "map";

}
