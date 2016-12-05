/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.environment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Emil Simon
 */

public abstract class Consts {
    public static final SimpleDateFormat log_timestamp_form = new SimpleDateFormat ("HH:mm:ss");
    public static final SimpleDateFormat log_date_form = new SimpleDateFormat ("dd_MM_");
    
    public static final String log_file_path_general = "logs/" + log_date_form.format(new Date ()) + "_log.stf";
    public static final String log_file_path_ent = "logs/" + log_date_form.format(new Date ()) + "_entity_log.stf";
    public static final String log_file_path_act = "logs/" + log_date_form.format(new Date ()) + "_actor_log.stf";
    public static final String log_file_path_map = "logs/" + log_date_form.format(new Date ()) + "_map_log.stf";
    public static final String log_file_path_script = "logs/" + log_date_form.format(new Date ()) + "_script_log.stf";
    
    public static final String settings_file_path = "res/data/settings.stf";
    public static final String loader_file_path = "res/data/loader.stf";
    public static final String actors_file_path = "res/data/actors/actors.stf";
    public static final String entities_file_path = "res/data/entities/entities.stf";
    public static final String actor_dump_folder = "res/data/actors/";
    public static final String entity_dump_folder = "res/data/entities/";
    public static final String trigger_dump_folder = "res/data/triggers/";
     
    public static final int tileset_frame_width = 32;
    public static final int tileset_frame_height = 32;
    
    public static final String actor_file_extension = "act";
    public static final String entity_file_extension = "ent";
    public static final String trigger_file_extension = "sts";
}
