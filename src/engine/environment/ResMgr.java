/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.environment;

import engine.logger.Log;
import engine.game.actors.Actor;
import engine.game.actors.AnimatedSprite;
import engine.game.entities.EntityType;
import engine.game.maps.Tileset;
import engine.game.triggers.Trigger;
import engine.game.triggers.TriggerEvent;
import engine.game.triggers.TriggerMgr;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;

/**
 * @author Emil Simon
 */

public abstract class ResMgr {
    public static HashMap<String, Sound> sound_lib;
    public static HashMap<String, AnimatedSprite> sprite_lib;
    public static HashMap<String, TrueTypeFont> font_lib;
    public static HashMap<String, EntityType> entity_lib;
    public static HashMap<String, Actor> actor_lib;
    public static HashMap<String, Tileset> tileset_lib;
    public static HashMap<String, Trigger> trigger_lib;
    
    
     
    public static class onExit extends Thread {
        @Override
        public void run () {
            Log.log(Log.GENERAL, "Program exited ...");
            Log.endLog();
        }
    }
    
    
    
    public static void init () {
        sound_lib = new HashMap<> ();
        sprite_lib = new HashMap<> ();
        font_lib = new HashMap<> ();
        entity_lib = new HashMap<> ();
        actor_lib = new HashMap<> ();
        tileset_lib = new HashMap<> ();
        trigger_lib = new HashMap<> ();
        
        try {
            Log.log(Log.GENERAL, "Reading loader file ...");
            readLoader();
            Log.log(Log.GENERAL, "Done!");
            
            Log.log(Log.GENERAL, "Reading settings file ...");
            if (Settings.force_default_settings) {
                Log.log(Log.GENERAL,"Force default settings detected, writing ...");
                writeSettings();
            }
            readSettings();
            Log.log(Log.GENERAL,"Done!");
            
            Log.log(Log.GENERAL,"Auto-detecting trigger scripts ...");
            loadScripts ();
            Log.log(Log.GENERAL,"Done!");
            
            Log.log(Log.GENERAL,"Reading actors file ...");
            readActors ();
            Log.log(Log.GENERAL,"Done!");
            
            Log.log(Log.GENERAL,"Reading entities file ...");
            readEntities ();
            Log.log(Log.GENERAL,"Done!");
            
        } catch (SlickException|IOException ex) {
            Log.log(Log.GENERAL,Log.LogLevel.FATAL,"while trying to initialize resources");
            System.exit(-1);
        }
    }
    
    
    
    public static void readLoader () throws FileNotFoundException, IOException, SlickException {
        //try to open and read settings
        BufferedReader br = new BufferedReader (new FileReader (Consts.LOADER_FILE_PATH));
        String line;

        while ((line=br.readLine ())!=null) {
            if (!line.startsWith("#") && !line.trim().isEmpty()) { // COMMENT
                String[] words = line.trim().split(":");
                if (words.length != 2)
                    Log.err(Log.GENERAL,"while trying to add '"+line.trim()+"'",null);
                else {
                    String given = words[1].trim();
                    String path = given.substring(given.indexOf("'")+1, given.lastIndexOf("'"));
                    given = given.replace(path, "_PATH_");
                    String[] vars = given.split(" ");
                    
                    switch (words[0]) {
                        case "sound" :
                            if (vars.length != 2)
                                Log.err(Log.GENERAL,"while trying to add '"+line.trim()+"' - number of variables must be 2",null);
                            else {
                                sound_lib.put(vars[0], new Sound (path));
                                Log.log(Log.GENERAL,"Loaded sound '" + vars[0] + "'");
                            }
                            break;
                        case "sprite" :
                            if (vars.length != 4)
                                Log.err(Log.GENERAL,"while trying to add '"+line.trim()+"' - number of variables must be 4",null);
                            else {
                                sprite_lib.put(vars[0], new AnimatedSprite (vars[0], path, Integer.parseInt(vars[2]), Integer.parseInt(vars[3])));
                                Log.log(Log.GENERAL,"Loaded sprite '" + vars[0] + "'");
                            }
                            break;
                        case "tileset" :
                            if (vars.length != 3)
                                Log.err(Log.GENERAL,"while trying to add '"+line.trim()+"' - number of variables must be 3",null);
                            else if ((Integer.parseInt(vars[2])>20) || (Integer.parseInt(vars[2])<0))
                                Log.err(Log.GENERAL,"while trying to add '"+line.trim()+"' - tileset ID must be unique",null);
                            else {
                                tileset_lib.put(vars[0], new Tileset (vars[0], path, Integer.parseInt(vars[2])));
                                Log.log(Log.GENERAL,"Loaded tileset '" + vars[0] + "'");
                            }
                            break;
                        case "font" :
                            if (vars.length != 4)
                                Log.err(Log.GENERAL,"while trying to add '"+line.trim()+"' - number of variables must be 4",null);
                            else {
                                int style = vars[3].equals("bold") ? Font.BOLD : (vars[3].equals("italic") ? Font.ITALIC : Font.PLAIN) ;
                                font_lib.put(vars[0], new TrueTypeFont (new Font (path, style, Integer.valueOf(vars[2])), false));
                                Log.log(Log.GENERAL,"Loaded font '" + vars[0] + "'");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    
     
    public static void readActors () throws FileNotFoundException, IOException {
        //try to open and read settings
        BufferedReader br = new BufferedReader (new FileReader (Consts.ACTORS_FILE_PATH));
        String line;

        while ((line=br.readLine ())!=null) {
            if (!line.startsWith("#") && !line.trim().isEmpty()) { // COMMENT
                String[] words = line.trim().split(":");
                if (words.length != 2)
                    Log.err(Log.GENERAL,"while trying to locate '"+words[0]+"' actor",null);
                else
                    try {
                        actor_lib.put(words[0], new Actor (words[0],words[1]));
                    } catch (IOException ex) {
                        Log.err(Log.GENERAL,"while trying to read actor file '"+words[1]+"' for actor '"+words[0]+"'",ex);
                    }
            }
        }
    }
     
    public static void writeActors () {
        String out = "# actor_name:res/data/actors/protagonist.act\n\n";
        
        for (int i=0;i<actor_lib.size();i++) {
            out += actor_lib.get((String)actor_lib.keySet().toArray()[i]).actor_name+":"+actor_lib.get((String)actor_lib.keySet().toArray()[i]).actor_file+"\n";
        }
        
        try {
            BufferedWriter bw = new BufferedWriter (new FileWriter (new File (Consts.ACTORS_FILE_PATH)));
            bw.write(out);
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Log.err(Log.GENERAL,"could not rewrite actors file ("+Consts.ACTORS_FILE_PATH+")",ex);
        }
    }
    
    
     
    public static void readEntities () throws FileNotFoundException, IOException {
        //try to open and read settings
        BufferedReader br = new BufferedReader (new FileReader (Consts.ENTITIES_FILE_PATH));
        String line;

        while ((line=br.readLine ())!=null) {
            if (!line.startsWith("#") && !line.trim().isEmpty()) { // COMMENT
                String[] words = line.trim().split(":");
                if (words.length != 2)
                    Log.err(Log.GENERAL,"while trying to locate '"+words[0]+"' entity",null);
                else
                    try {
                        entity_lib.put(words[0], new EntityType (words[0],words[1]));
                    } catch (Exception ex) {
                        Log.err(Log.GENERAL,"while trying to read entity file '"+words[1]+"' for entity type '"+words[0]+"'",ex);
                    }
            }
        }
    }
     
    public static void writeEntities () {
        String out = "# entity_name:res/data/entities/protagonist.ent\n\n";
        
        for (int i=0;i<entity_lib.size();i++) {
            out += entity_lib.get((String)entity_lib.keySet().toArray()[i]).entity_type_name+":"+entity_lib.get((String)entity_lib.keySet().toArray()[i]).entity_type_file+"\n";
        }
        
        try {
            BufferedWriter bw = new BufferedWriter (new FileWriter (new File (Consts.ENTITIES_FILE_PATH)));
            bw.write(out);
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Log.err(Log.GENERAL,"could not rewrite entities file ("+Consts.ENTITIES_FILE_PATH+")",ex);
        }
    }
    
    
    
    public static void loadScripts () {
        TriggerMgr.init();
        TriggerMgr.autoDetectTriggers ();
    }
    
    
    
    public static void readSettings () {
        Settings.readSettings();
    }
    
    public static void writeSettings () {
        Settings.writeSettings();
    }
    
    
    
    public static boolean playSound (String sound) {
        if (hasSound(sound)) {
            getSound(sound).play(1.0f, Settings.sfx_volume);
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean playMusic (String music) {
        if (hasSound(music)) {
            getSound(music).play(1.0f, Settings.music_volume);
            return true;
        } else {
            return false;
        }
    }
    
    
    
    public static EntityType getEntityType (String entity) {
        if (entity_lib.containsKey(entity))
            return entity_lib.get(entity);
        else
            return null;
    }
    public static Actor getActor (String actor) {
        if (actor_lib.containsKey(actor))
            return actor_lib.get(actor);
        else
            return null;
    }
    public static AnimatedSprite getAnimatedSprite (String sprite) {
        if (sprite_lib.containsKey(sprite))
            return sprite_lib.get(sprite);
        else
            return null;
    }
    public static SpriteSheet getSprite (String sprite) {
        if (sprite_lib.containsKey(sprite))
            return sprite_lib.get(sprite).sheet;
        else
            return null;
    }
    public static Sound getSound (String sound) {
        if (sound_lib.containsKey(sound))
            return sound_lib.get(sound);
        else
            return null;
    }
    public static TrueTypeFont getFont (String font) {
        if (font_lib.containsKey(font))
            return font_lib.get(font);
        else
            return null;
    }
    public static Tileset getTileset (String tileset) {
        if (tileset_lib.containsKey(tileset))
            return tileset_lib.get(tileset);
        else
            return null;
    }
    
    
    
    public static boolean hasEntityType (String entity) {
        return entity_lib.containsKey(entity);
    }
    public static boolean hasActor (String actor) {
        return actor_lib.containsKey(actor);
    }
    public static boolean hasAnimatedSprite (String sprite) {
        return sprite_lib.containsKey(sprite);
    }
    public static boolean hasSprite (String sprite) {
        return sprite_lib.containsKey(sprite);
    }
    public static boolean hasSound (String sound) {
        return sound_lib.containsKey(sound);
    }
    public static boolean hasFont (String font) {
        return font_lib.containsKey(font);
    }
    public static boolean hasTileset (String tileset) {
        return tileset_lib.containsKey(tileset);
    }
    
    
    
    public static void addEntityType (String name, EntityType entity) {
        entity_lib.put(name, entity);
    }
    public static void addActor (String name, Actor actor) {
        actor_lib.put(name, actor);
    }
    public static void addAnimatedSprite (String name, AnimatedSprite sprite) {
        sprite_lib.put(name, sprite);
    }
    public static void addSound (String name, Sound sound) {
        sound_lib.put(name, sound);
    }
    public static void addFont (String name, TrueTypeFont font) {
        font_lib.put(name, font);
    }
    public static void addTileset (String name, Tileset tileset) {
        tileset_lib.put(name, tileset);
    }
    
    
    
    public static void deleteActor (String actor) {
        File f = new File (ResMgr.getActor(actor).actor_file);
        if (f.exists())
            f.delete();
        
        ResMgr.actor_lib.remove(actor);
        writeActors();
    }
    public static void deleteEntityType (String entity) {
        File f = new File (ResMgr.getEntityType(entity).entity_type_file);
        if (f.exists())
            f.delete();
        
        ResMgr.entity_lib.remove(entity);
        writeEntities();
    }
}
