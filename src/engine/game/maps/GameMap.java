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
import java.util.Arrays;
import java.util.HashMap;
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
    public int tiles_width, tiles_height; // IN TILES
    public Camera cam;
    public TileNet tile_net;
    public HashMap <String, Entity> entities;
    
    public String devmode_current_tileset;
    public static final int info_draw_offset_x = 32;
    public static final int info_draw_offset_y = 64;
    
    
     
    public GameMap (int width, int height) {
        tiles_width = width;
        tiles_height = height;
        
        entities = new HashMap <> ();
        tile_net = new TileNet (width,height);
        cam = new Camera ();
        
        background_tileset = "";
        devmode_current_tileset = "";
        tile_net = new TileNet (width, height);
    }
    
    
    
    public void drawInfo (GameContainer gc, StateBasedGame sbg, Graphics g) {
        g.setColor(Color.white);
        g.drawString("Camera: "+cam.location.toString(), info_draw_offset_x, info_draw_offset_y);
        g.drawString("devmode_current_tileset: "+devmode_current_tileset, info_draw_offset_x, info_draw_offset_y+32);
        g.drawString("background_tileset: "+background_tileset, info_draw_offset_x, info_draw_offset_y+64);
    }
    
    public void render (GameContainer gc, StateBasedGame sbg, Graphics g) {
        g.setColor(Color.white);

        int start_render_x = (int)(Math.max(cam.location.x/Consts.tileset_frame_width, 0));
        int end_render_x = (int)(Math.max(cam.location.x/Consts.tileset_frame_width, 0)) + (Settings.screen_res_w/Consts.tileset_frame_width) + 1;
        int start_render_y = (int)(Math.max(cam.location.y/Consts.tileset_frame_height, 0));
        int end_render_y = (int)(Math.max(cam.location.y/Consts.tileset_frame_height, 0)) + (Settings.screen_res_h/Consts.tileset_frame_height) + 2;
        
        if ((!background_tileset.isEmpty())) {
            for (int i=start_render_y;i<end_render_y;i++) {
                for (int j=start_render_x;j<end_render_x;j++) {
                    if (!((i<0) || (i>tiles_height-1) || (j<0) || (j>tiles_width-1))) {
                        Image img = ResMgr.getTileset(background_tileset).getTile(true,true,true,true);
                        if (img!=null)
                            g.drawImage(img,
                                    j*Consts.tileset_frame_width - cam.location.x,
                                    i*Consts.tileset_frame_height - cam.location.y);
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
                                        j*Consts.tileset_frame_width - cam.location.x,
                                        i*Consts.tileset_frame_height - cam.location.y);
                                        //j-start_render_x-(cam.location.x % ResMgr.tileset_frame_width) ,
                                        //i-start_render_y-(cam.location.y % ResMgr.tileset_frame_height));
                        }
                    }
                }

                if (devmode_current_tileset!=null) {
                    if ((!devmode_current_tileset.isEmpty())) {
                        tile_net.render(gc, sbg, g, cam, devmode_current_tileset);
                    }
                }
            }
        }
    }
    
    
    
    public void update(GameContainer gc, StateBasedGame sbg, int i) {
        
    }
    
    
    
    public String getWritten () {
        String content = "";
        content += "#"+name+" map :" + "\n\n\n";
        
        content += "#name:map_name" + "\n";
        content += "name:"+name + "\n\n\n";
        
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
        for (int i=0;i<entityNames.length;i++) {
            content += "entity define" + "\n";
            content += entities.get(entityNames[i]).getWritten("\t") + "\n";
            content += "entity end" + "\n";
        }
        content += "\n\n";
        
        return content;
    }
}
