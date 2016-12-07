/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.environment;

import engine.logger.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Emil Simon
 */

public abstract class Settings {
    public static String title = "GameProject";
    public static float sfx_volume = 1f;
    public static float music_volume = 1f;
    public static int screen_res_w = 800;
    public static int screen_res_h = 600;
    public static boolean grfx_vsync = false;
    public static boolean fullscreen = false;
    public static boolean debug_actors = true;
    public static boolean debug_entities = true;
    public static boolean debug_maps = true;
    public static boolean debug_triggers = true;
    public static boolean debug_draw_tile_net = true;
    public static boolean devmode = true;
    
    public static boolean force_default_settings = false;
    
    

    public static void readSettings () {
        try {
            //try to open and read settings
            BufferedReader br = new BufferedReader (new FileReader (Consts.SETTINGS_FILE_PATH));
            String line;

            while ((line=br.readLine ())!=null) {
                if (!line.startsWith("#")) { // COMMENT
                    String[] lines=line.split(" ");
                    switch (lines[0]) {
                        case "title" :
                            title = line.substring(line.indexOf('\'')+1, line.lastIndexOf('\''));
                            Log.log(Log.GENERAL,"Title set to "+title);
                            break;
                        case "sfx_volume" :
                            sfx_volume = Float.valueOf(lines[1]);
                            Log.log(Log.GENERAL,"SFX Volume set to "+sfx_volume);
                            break;
                        case "music_volume" :
                            music_volume = Float.valueOf(lines[1]);
                            Log.log(Log.GENERAL,"Music Volume set to "+music_volume);
                            break;
                        case "resolution" :
                            String[] res = lines[1].split("x");
                            screen_res_w = Integer.valueOf(res[0]);
                            screen_res_h = Integer.valueOf(res[1]);
                            Log.log(Log.GENERAL,"Screen Resolution set to "+screen_res_w+"x"+screen_res_h);
                            break;
                        case "fullscreen" :
                            fullscreen = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Fullscreen Mode set to "+fullscreen);
                            break;
                        case "vsync" :
                            grfx_vsync = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"VSync set to "+grfx_vsync);
                            break;
                        case "debug_actors" :
                            debug_actors = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Actor Debug Mode set to "+debug_actors);
                            break;
                        case "debug_entities" :
                            debug_entities = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Entity Debug Mode set to "+debug_entities);
                            break;
                        case "debug_maps" :
                            debug_entities = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Map Debug Mode set to "+debug_entities);
                            break;
                        case "debug_triggers" :
                            debug_entities = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Trigger Debug Mode set to "+debug_entities);
                            break;
                        case "debug_draw_tile_net" :
                            debug_draw_tile_net = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Draw TileNet Debug Mode set to "+debug_draw_tile_net);
                            break;
                        case "devmode" :
                            devmode = Integer.valueOf(lines[1])>0;
                            Log.log(Log.GENERAL,"Dev Mode set to "+devmode);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException | NumberFormatException ex) {
            //use default settings and create the file if it doesnt exist
            File settings_file = new File (Consts.SETTINGS_FILE_PATH);
            if (!settings_file.exists()) {
                Log.log(Log.GENERAL,"ERR: Settings file not found!");
                try {
                    settings_file.createNewFile();
                } catch (IOException ex1) {
                    Log.log (Log.GENERAL,"ERR: while trying to create default settings file");
                    System.exit(-1);
                }
            }

            writeSettings();
        }
    }

    public static void writeSettings () {
        File settings_file = new File (Consts.SETTINGS_FILE_PATH);
        try {
            Writer w = new BufferedWriter (new FileWriter(settings_file, false));
            w.write("# game settings file" + "\n" +
                    "title '" + title + "'\n" +
                    "sfx_volume " + sfx_volume + "\n" +
                    "music_volume " + music_volume + "\n" +
                    "resolution " + screen_res_w + "x" + screen_res_h + "\n" +
                    "fullscreen " + (fullscreen?"1":"0") + "\n" +
                    "vsync " + (grfx_vsync?"1":"0") + "\n" +
                    "debug_actors " + (debug_actors?"1":"0") + "\n" +
                    "debug_entities " + (debug_entities?"1":"0") + "\n" +
                    "debug_maps " + (debug_maps?"1":"0") + "\n" +
                    "debug_triggers " + (debug_triggers?"1":"0") + "\n" +
                    "debug_draw_tile_net " + (debug_draw_tile_net?"1":"0") + "\n" +
                    "devmode " + (devmode?"1":"0") + "\n");
            Log.log(Log.GENERAL,"Settings file was created!");
            w.close();
        } catch (IOException e) {
            Log.err(Log.GENERAL,"while trying to write settings file",e);
            System.exit(-1);
        }
    }
}
