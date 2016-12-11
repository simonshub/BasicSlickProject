/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.maps;

import engine.game.entities.Entity;
import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.environment.Settings;
import engine.game.entities.EntityType;
import engine.logger.Log;
import engine.utils.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Emil Simon
 */

public class GameMap {
    public String name;
    public String background_tileset;
    
    public Camera cam;
    public TileNet tile_net;
    public boolean persistent;
    public int tiles_width, tiles_height; // IN TILES
    public HashMap <String, Entity> entities;
    public HashMap <String, Integer> entity_counters;
    
    public String devmode_current_tileset;
    public static final int INFO_DRAW_OFFSET_X = 32;
    public static final int INFO_DRAW_OFFSET_Y = 64;
    public static final int INFO_DRAW_INCREMENT_Y = 24;
    
    
     
    public GameMap (String name, int width, int height, boolean persistent) {
        tiles_width = width;
        tiles_height = height;
        
        this.name = name;
        this.persistent = persistent;
        
        cam = new Camera ();
        entities = new HashMap <> ();
        entity_counters = new HashMap <> ();
        
        background_tileset = "";
        devmode_current_tileset = "";
        tile_net = new TileNet (width, height);
    }
    
    public GameMap (File f) {
        cam = new Camera ();
        entities = new HashMap <> ();
        entity_counters = new HashMap <> ();
        background_tileset = "";
        devmode_current_tileset = "";
        
        try {
            BufferedReader br = new BufferedReader (new FileReader (f));
            List<String> lines = new ArrayList<> ();
            String line;
            while ((line=br.readLine())!=null) {
                lines.add(line);
            }
            this.fromWritten(StringUtils.listToArray(lines));
        } catch (FileNotFoundException ex) {
            Log.err(Log.MAP, "error while loading map from file '"+f.getPath()+"'", ex);
        } catch (IOException ex) {
            Log.err(Log.MAP, "error while loading map from file '"+f.getPath()+"'", ex);
        }
    }
    
    
    
    public boolean canPlaceEntity (EntityType entity_type, int x, int y) {
        for (Entity e : entities.values()) {
            if (e.intersectsCollider(entity_type, x, y))
                return false;
        }
        return true;
    }
    
    public void placeEntity (EntityType entity_type, int x, int y) {
        int counter = 0;
        
        if (entity_counters.containsKey(entity_type.entity_type_name)) {
            counter = entity_counters.get(entity_type.entity_type_name)+1;
            entity_counters.put(entity_type.entity_type_name, counter);
        } else {
            entity_counters.put(entity_type.entity_type_name, 0);
        }
        
        entities.put(entity_type.entity_type_name+"_"+String.format("%06d",counter), new Entity (entity_type, counter, x, y));
        Log.log(Log.MAP, "placed entity '"+entity_type.entity_type_name+"_"+String.format("%06d",counter)+"' at "+x+","+y+" of type '"+entity_type.entity_type_name+"'");
    }
    
    public void destroyEntity (Entity ent) {
        if ((ent != null) && (entities.containsValue(ent))) {
            entities.remove(ent.name);
        }
    }
    
    public void changeEntityName (String old_name, String new_name) {
        if (entities.containsKey(old_name)) {
            Entity e = entities.get(old_name);
            entities.remove(old_name);
            e.name = new_name;
            entities.put(new_name, e);
        } else {
            Log.log(Log.MAP, Log.LogLevel.ERROR, "could not rename entity '"+old_name+"' because it doesn't exist!");
        }
    }
    
    public Entity getMouseOverEntity (GameContainer gc) {
        Entity ent = null;
        for (Entity e : entities.values()) {
            if (e.isPointInside((int)(gc.getInput().getMouseX()*cam.zoom) + cam.location.x, (int)(gc.getInput().getMouseY()*cam.zoom) + cam.location.y)) {
                ent = e;
                break;
            }
        }
        return ent;
    }
    
    
    
    public void drawInfo (GameContainer gc, StateBasedGame sbg, Graphics g) {
        g.setColor(Color.white);
        g.drawString("devmode_current_tileset: "+devmode_current_tileset, INFO_DRAW_OFFSET_X, INFO_DRAW_OFFSET_Y);
        g.drawString("background_tileset: "+background_tileset, INFO_DRAW_OFFSET_X, INFO_DRAW_OFFSET_Y+32);
    }
    
    public void render (GameContainer gc, StateBasedGame sbg, Graphics g) {
        g.setColor(Color.white);

        int start_render_x = (int)(Math.max(cam.location.x/Consts.TILESET_FRAME_WIDTH*cam.zoom, 0));
        int end_render_x = (int)((Math.max(cam.location.x/Consts.TILESET_FRAME_WIDTH*cam.zoom, 0)) + (Settings.screen_res_w/Consts.TILESET_FRAME_WIDTH*cam.zoom)) + 1;
        int start_render_y = (int)(Math.max(cam.location.y/Consts.TILESET_FRAME_HEIGHT*cam.zoom, 0));
        int end_render_y = (int)((Math.max(cam.location.y/Consts.TILESET_FRAME_HEIGHT*cam.zoom, 0)) + (Settings.screen_res_h/Consts.TILESET_FRAME_HEIGHT*cam.zoom)) + 2;
        
        if ((!background_tileset.isEmpty())) {
            for (int i=start_render_y;i<end_render_y;i++) {
                for (int j=start_render_x;j<end_render_x;j++) {
                    if (!((i<0) || (i>tiles_height-1) || (j<0) || (j>tiles_width-1))) {
                        Image img = ResMgr.getTileset(background_tileset).getTile(true,true,true,true);
                        if (img!=null)
                            g.drawImage(img,
                                    j*Consts.TILESET_FRAME_WIDTH*cam.zoom - cam.location.x,
                                    i*Consts.TILESET_FRAME_HEIGHT*cam.zoom - cam.location.y);
                    }
                }
            }
        }
        
        for (String tileset_render : Arrays.copyOf(ResMgr.tileset_lib.keySet().toArray(), ResMgr.tileset_lib.size(), String[].class)) {
            if (!tileset_render.equals(background_tileset)) {
                for (int i=start_render_y;i<end_render_y;i++) {
                    for (int j=start_render_x;j<end_render_x;j++) {
                        if (!((i<0) || (i>tiles_height-1) || (j<0) || (j>tiles_width-1))) {
                            Image img = ResMgr.getTileset(tileset_render)
                                        .getTile(tile_net.renderTile(tileset_render,  j,  i),tile_net.renderTile(tileset_render,j+1,  i),
                                                 tile_net.renderTile(tileset_render,  j,i+1),tile_net.renderTile(tileset_render,j+1,i+1));
                            if (img!=null)
                                g.drawImage(img,
                                        j*Consts.TILESET_FRAME_WIDTH*cam.zoom - cam.location.x,
                                        i*Consts.TILESET_FRAME_HEIGHT*cam.zoom - cam.location.y);
                                        //j-start_render_x-(cam.location.x % ResMgr.tileset_frame_width) ,
                                        //i-start_render_y-(cam.location.y % ResMgr.tileset_frame_height));
                        }
                    }
                }

                if (devmode_current_tileset!=null) {
                    if ((!devmode_current_tileset.isEmpty())) {
//                        if (tile_net!=null)
                            tile_net.render(gc, sbg, g, cam, devmode_current_tileset);
                    }
                }
            }
        }
        
        
        
//        Entity[] sorted_ents = (Entity[]) entities.values().toArray();
//        Arrays.sort(sorted_ents);
        List<Entity> sorted_ents = new ArrayList<> (entities.values());
        Collections.sort(sorted_ents);
        sorted_ents.stream().forEach((entity) -> {
            entity.render(gc, sbg, g, cam);
        });
        
        if (Settings.debug_draw_entity_debug) {
            sorted_ents.stream().forEach((entity) -> {
                entity.renderDebug(gc, sbg, g, cam, new Color (1f,1f,1f,0.5f));
            });
        }
    }
    
    
    
    public void update(GameContainer gc, StateBasedGame sbg, int i) {
        
    }
    
    
    
    public String getWritten () {
        String content = "";
        content += "#"+name+" map :" + "\n\n\n";
        
        content += "#name:map_name" + "\n";
        content += "name:"+name + "\n\n\n";
        
        content += "#background_tileset:tileset_name" + "\n";
        content += "background_tileset:"+background_tileset + "\n\n";
        
        content += "#persistent:true" + "\n";
        content += "persistent:"+String.valueOf(persistent) + "\n\n";
        
        content += "#tile_net define" + "\n";
        content += "#\tgenerated tile_net info" + "\n";
        content += "#tile_net end" + "\n";
        content += "tile_net define" + "\n";
        content += tile_net.getWritten("\t") + "\n";
        content += "tile_net end" + "\n";
        
        content += "#entity define" + "\n";
        content += "#\tgenerated entity info" + "\n";
        content += "#entity end" + "\n";
        String[] entityNames = entities.keySet().toArray(new String [entities.size()]);
        for (String entityName : entityNames) {
            content += "entity define" + "\n";
            content += entities.get(entityName).getWritten("\t") + "\n";
            content += "entity end" + "\n";
        }
        content += "\n\n";
        
        return content;
    }
    
    public final void fromWritten (String[] lines) {
        boolean read_for_entity=false;
        boolean read_for_tiles=false;
        List<String> entity_lines = new ArrayList<> ();
        List<String> tiles_lines = new ArrayList<> ();
        
        for (String line : lines) {
            if (!line.startsWith("#")) {
                if (read_for_entity) {
                    if (line.trim().contains("entity end")) {
                        read_for_entity = false;
                        Entity e = new Entity (StringUtils.listToArray(entity_lines));
                        entities.put(e.name, e);
                        entity_lines.clear();
                        Log.log(Log.MAP, "added entity '"+e.name+"'");
                    } else {
                        entity_lines.add(line);
                    }
                } else if (read_for_tiles) {
                    if (line.trim().contains("tile_net end")) {
                        read_for_tiles = false;
                        this.tile_net = new TileNet (StringUtils.listToArray(tiles_lines));
                        tiles_lines.clear();
                        
                        this.tiles_width = tile_net.width;
                        this.tiles_height = tile_net.height;
                        
                        Log.log(Log.MAP, "read tilenet info ...");
                    } else {
                        tiles_lines.add(line);
                    }
                } else

                if (line.trim().contains("entity define")) {
                    read_for_entity = true;
                    Log.log(Log.MAP, "defining entity ...");
                } else if (line.trim().contains("tile_net define")) {
                    read_for_tiles = true;
                    Log.log(Log.MAP, "defining tilenet ...");
                } else

                if (line.trim().startsWith("name")) {
                    this.name = line.trim().substring(line.trim().indexOf(":")+1).trim();
                } else if (line.trim().startsWith("background_tileset")) {
                    this.background_tileset = line.trim().substring(line.trim().indexOf(":")+1).trim();
                } else if (line.trim().startsWith("persistent")) {
                    this.persistent = Boolean.parseBoolean(line.trim().substring(line.trim().indexOf(":")+1).trim());
                }
            }
        }
        
        Log.log(Log.MAP, "map '"+name+"' finished loading!");
    }
    
    public boolean save ()  {
        String file_name = Consts.MAP_DUMP_FOLDER + name + "." + Consts.MAP_FILE_EXTENSION;
        
        try (BufferedWriter bw = new BufferedWriter (new FileWriter (new File (file_name)))) {
            bw.write(this.getWritten());
            bw.flush();
        } catch (IOException e) {
            Log.log(Log.MAP, Log.LogLevel.ERROR, "while saving map '"+name+"' to file '"+file_name+"'");
            return false;
        }
        
        Log.log(Log.MAP, "saved map '"+name+"' in file '"+file_name+"'");
        return true;
    }
}
