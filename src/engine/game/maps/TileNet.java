/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.maps;

import engine.environment.Consts;
import engine.logger.Log;
import engine.environment.ResMgr;
import engine.environment.Settings;
import java.util.Arrays;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Emil Simon
 */

public class TileNet {
    public String[] tilesets;
    public int[][] map;
    public int width, height;
     
    
    
    public TileNet (int width, int height) {
        map = new int [height+1][width+1];
        
        this.width = width;
        this.height = height;
        this.tilesets = Arrays.copyOf(ResMgr.tileset_lib.keySet().toArray(), ResMgr.tileset_lib.size(), String[].class);
    }
    
    public TileNet (String[] lines) {
        this.tilesets = Arrays.copyOf(ResMgr.tileset_lib.keySet().toArray(), ResMgr.tileset_lib.size(), String[].class);
        
        boolean readingMap = false;
        String mapInfo = "";
        for (String line : lines) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                // COMMENT
                if (!readingMap) {
                    String[] words = line.trim().split(":");
                    switch (words[0].trim()) {
                        case "width" :
                            width = Integer.parseInt(words[1].trim());
                            break;
                        case "height" :
                            height = Integer.parseInt(words[1].trim());
                            break;
                        case "map" :
                            readingMap = true;
                            break;
                        default :
                            break;
                    }
                } else {
                    mapInfo += line;
                }
            }
        }
        readMap(mapInfo);
    }
    
    
    
    public void fill (int x, int y, String tileset) {
        if ((x < 0) || (x > width) || (y < 0) || (y > height))
            return;
        
        String fillTileset = getTilesetAt (x,y);
        int tileset_id = getTilesetId (tileset);
        if (map[y][x] != tileset_id)
            map[y][x] = tileset_id;
        
        try {
            fillNode (x+1,y, tileset, fillTileset);
            fillNode (x,y+1, tileset, fillTileset);
            fillNode (x-1,y, tileset, fillTileset);
            fillNode (x,y-1, tileset, fillTileset);
        } catch (StackOverflowError e) {
            Log.log(Log.MAP,Log.LogLevel.ERROR,"STACK OVERFLOW ERROR - while filling a huge area of the map.",true);
        }
    }
    
    public void fillNode (int x, int y, String tileset, String fillTileset)  {
        if ((x < 0) || (x > width) || (y < 0) || (y > height))
            return;
        
        int tileset_id = getTilesetId (tileset);
        int fill_tileset_id = getTilesetId (fillTileset);
        
        if (((map[y][x]==fill_tileset_id) || (fill_tileset_id==tileset_id)) && (map[y][x] != tileset_id)) {
            map[y][x] = tileset_id;
            fillNode (x+1,y, tileset, fillTileset);
            fillNode (x,y+1, tileset, fillTileset);
            fillNode (x-1,y, tileset, fillTileset);
            fillNode (x,y-1, tileset, fillTileset);
        }
    }
    
    
    
    public int getTilesetId (String tileset) {
        int tileset_id = -1;
        for (int i=0;i<tilesets.length;i++) {
            if (tilesets[i].equals(tileset)) {
                tileset_id = i;
                break;
            }
        }
        return tileset_id;
    }
    
    public boolean renderTile (String tileset, int x, int y) {
        return getTilesetId(tileset) == map[y][x];
    }
    
    public String getTilesetAt (int x, int y) {
        if ((x < 0) || (x > width) || (y < 0) || (y > height))
            return "ERR#94kvde";
        return tilesets[map[y][x]];
    }
    
    
    
    public String getWritten (String prefix) {
        String content = "";
        
        content += prefix + "width:"+width + "\n";
        content += prefix + "height:"+height + "\n\n";
        
        content += prefix + "map:" + "\n";
        for (int i=0;i<=height;i++) {
            content += prefix + "\t";
            for (int j=0;j<=width;j++) {
                content += String.valueOf(map[i][j]) + " ";
            }
            content += "\n";
        }
        content += "\n";
        
        return content;
    }
    
    public final void readMap (String content) {
        content = content.trim();
        String[] args = content.split(" ");
        for (int i=0;i<=height;i++) {
            for (int j=0;j<=width;j++) {
                map[i][j] = Integer.parseInt(args[i*height+j]);
            }
        }
    }
    
    
     
    public void render (GameContainer gc, StateBasedGame sbg, Graphics g, Camera c, String debug_tileset) {
        if (Settings.debug_draw_tile_net) {
            g.setColor(Color.white);
            
            int start_render_x = (int)(Math.max(c.location.x/Consts.tileset_frame_width, 0));
            int end_render_x = (int)(Math.max(c.location.x/Consts.tileset_frame_width, 0)) + (Settings.screen_res_w/Consts.tileset_frame_width) + 1;
            int start_render_y = (int)(Math.max(c.location.y/Consts.tileset_frame_height, 0));
            int end_render_y = (int)(Math.max(c.location.y/Consts.tileset_frame_height, 0)) + (Settings.screen_res_h/Consts.tileset_frame_height) + 2;
            int tileset_id = getTilesetId (debug_tileset);
            
            for (int i=start_render_y;i<end_render_y;i++) {
                for (int j=start_render_x;j<end_render_x;j++) {
                    if (!((i<0) || (i>height) || (j<0) || (j>width))) {
                        if (map[i][j] != tileset_id)
                            g.drawRect(j*Consts.tileset_frame_width-2 - c.location.x, i*Consts.tileset_frame_height-2 - c.location.y, 4, 4);
                        else
                            g.fillRect(j*Consts.tileset_frame_width-2 - c.location.x, i*Consts.tileset_frame_height-2 - c.location.y, 4, 4);
                    }
                }
            }
        }
    }
}
