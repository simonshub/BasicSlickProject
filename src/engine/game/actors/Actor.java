/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.actors;

import engine.environment.Consts;
import engine.logger.Log;
import engine.environment.ResMgr;
import engine.environment.Settings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;

/**
 * @author Emil Simon
 */
public class Actor {
    public String actor_name;
    public String actor_file;
    public String sheet;
    public String default_anim;
    
    public HashMap<String, Animation> anims;
    public List<AnimFrame> anim_frame_list;
    
    
    
    public Actor (String actorName) throws IOException {
        actor_name = actorName;
        actor_file = Consts.ACTOR_DUMP_FOLDER + actor_name + "." + Consts.ACTOR_FILE_EXTENSION;
        sheet = "";
        anims = new HashMap <> ();
        anim_frame_list = new ArrayList <> ();
        
        File f = new File (actor_file);
        if (!f.exists()) {
            f.createNewFile();
        }
    }
     
    public Actor (String actorName, String actorFilePath) throws FileNotFoundException, IOException {
        actor_name = actorName;
        actor_file = actorFilePath;
        sheet = "";
        anims = new HashMap <> ();
        
        //try to open and read settings
        BufferedReader br = new BufferedReader (new FileReader (actorFilePath));
        String line;
        String current_animation = "";
        default_anim = "";
        anim_frame_list = new ArrayList <> ();
        
        if (Settings.debug_actors) Log.log(Log.ACTOR,"for actor '"+actor_name+"'");
        while ((line=br.readLine ())!=null) {
            if (!line.startsWith("#") && !line.isEmpty()) { // COMMENT
                String[] words = line.trim().split(":");
                switch (words[0].trim()) {
                    case "sprite" :
                        sheet = words[1].trim();
                        if (Settings.debug_actors)
                            Log.log(Log.ACTOR,"using sprite '"+sheet+"'");
                        break;

                    case "anim" :
                        current_animation = words[1].trim();
                        break;
                        
                    case "default_anim" :
                        default_anim = words[1].trim();
                        break;

                    case "frame" :
                        String[] vars = words[1].trim().split(" ");
                        if (vars.length == 3)
                            if (current_animation.isEmpty()) {
                                Log.log(Log.ACTOR,Log.LogLevel.ERROR,"adding frame without an animation defined - ignoring line");
                            } else {
                                anim_frame_list.add(new AnimFrame (current_animation, Integer.parseInt(vars[0]), Integer.parseInt(vars[1]), Integer.parseInt(vars[2])));
                                if (Settings.debug_actors) Log.log(Log.ACTOR,"added frame "+vars[1]+","+vars[2]+" to anim '"+current_animation+"'");
                            }
                        else
                            Log.log(Log.ACTOR,Log.LogLevel.ERROR,"not enough variables for new frame - ignoring line");
                        break;

                    default :
                        if (!words[0].isEmpty())
                            Log.log(Log.ACTOR,Log.LogLevel.ERROR,"unrecognized property in actor file '"+actorFilePath+"' -> property : '"+words[0]+"'");
                        break;
                }
            }
        }
        
        if (anim_frame_list.isEmpty()) {
            Log.log(Log.ACTOR,Log.LogLevel.ERROR,"no frames found!");
            return;
        }
        
        if (default_anim.isEmpty())
            default_anim = anim_frame_list.get(0).animation;
        
        for (int i=0;i<anim_frame_list.size();i++) {
            String anim = anim_frame_list.get(i).animation;
            int dur = anim_frame_list.get(i).dur;
            int x = anim_frame_list.get(i).x;
            int y = anim_frame_list.get(i).y;

            if (anims.containsKey(anim)) {
                anims.get(anim).addFrame(ResMgr.getSprite(sheet).getSubImage(x, y), dur);
            } else {
                anims.put(anim, new Animation ());
                anims.get(anim).setAutoUpdate(true);
                anims.get(anim).addFrame(ResMgr.getSprite(sheet).getSubImage(x, y), dur);
            }
        }
    }
    
    
    
    public void addFrame (String anim, int dur, int x, int y) {
        if (!ResMgr.sprite_lib.containsKey(sheet))
            return;
        
        if (anims.containsKey(anim)) {
            if ((ResMgr.getSprite(sheet).getHorizontalCount() > x) &&
                    (ResMgr.getSprite(sheet).getVerticalCount() > y)) {
                anim_frame_list.add(new AnimFrame (anim, dur, x, y));
                anims.get(anim).addFrame(ResMgr.getSprite(sheet).getSubImage(x, y), dur);
            }
        } else {
            addAnim (anim);
            if ((ResMgr.getSprite(sheet).getHorizontalCount() > x) &&
                    (ResMgr.getSprite(sheet).getVerticalCount() > y)) {
                anim_frame_list.add(new AnimFrame (anim, dur, x, y));
                anims.get(anim).addFrame(ResMgr.getSprite(sheet).getSubImage(x, y), dur);
            }
        }
    }
    public void addFrame (AnimFrame frame) {
        if (!ResMgr.sprite_lib.containsKey(sheet))
            return;
        
        if (anims.containsKey(frame.animation)) {
            if ((ResMgr.getSprite(sheet).getHorizontalCount() > frame.x) &&
                    (ResMgr.getSprite(sheet).getVerticalCount() > frame.y)) {
                anim_frame_list.add(new AnimFrame (frame.animation, frame.dur, frame.x, frame.y));
                anims.get(frame.animation).addFrame(ResMgr.getSprite(sheet).getSubImage(frame.x, frame.y), frame.dur);
            }
        } else {
            addAnim (frame.animation);
            if ((ResMgr.getSprite(sheet).getHorizontalCount() > frame.x) &&
                    (ResMgr.getSprite(sheet).getVerticalCount() > frame.y)) {
                anim_frame_list.add(new AnimFrame (frame.animation, frame.dur, frame.x, frame.y));
                anims.get(frame.animation).addFrame(ResMgr.getSprite(sheet).getSubImage(frame.x, frame.y), frame.dur);
            }
        }
    }
    
    public AnimFrame[] getAnimFrames (String whichAnim) {
        List<AnimFrame> sets = new ArrayList <> ();

        for (int i=0;i<anim_frame_list.size();i++) {
            if (anim_frame_list.get(i).animation.equals(whichAnim)) {
                sets.add(new AnimFrame (anim_frame_list.get(i)));
            }
        }

        AnimFrame[] return_sets = new AnimFrame [sets.size()];
        sets.toArray(return_sets);
        return return_sets;
    }
    public void reAddFrames (String anim) {
        addAnim(anim);
        for (int i=0;i<anim_frame_list.size();i++) {
            if (anim_frame_list.get(i).animation.equals(anim)) {
                anims.get(anim).addFrame(ResMgr.getSprite(sheet).getSubImage(anim_frame_list.get(i).x, anim_frame_list.get(i).y), anim_frame_list.get(i).dur);
            }
        }
    }
    public void removeFrame (String anim, int index) {
        int counter=0;
        for (int i=0;i<anim_frame_list.size();i++) {
            if (anim_frame_list.get(i).animation.equals(anim)) {
                if (counter==index) {
                    anim_frame_list.remove(i);
                    break;
                } else {
                    counter++;
                }
            }
        }
        reAddFrames(anim);
    }
    
    public void addAnim (String anim) {
        anims.put(anim, new Animation ());
        anims.get(anim).setAutoUpdate(true);
    }
    public void removeAnim (String anim) {
        for (int i=0;i<anim_frame_list.size();i++) {
            if (anim_frame_list.get(i).animation.equals(anim)) {
                anim_frame_list.remove(i);
                i--;
            }
        }
        anims.remove(anim);
    }
    
     
    public String getWritten () {
        String content = "";
        content += "#"+actor_name+" actor file:" + "\n\n\n";
        content += "#sprite:name" + "\n";
        content += "sprite:"+sheet + "\n\n\n";
            
        content += "#default_anim:name" + "\n";
        content += "default_anim:" + default_anim + "\n\n";
        
        for (int i=0;i<anims.size();i++) {
            String a = (String) anims.keySet().toArray()[i];
            
            content += "#anim:name" + "\n";
            content += "anim:" + a + "\n\n";
            
            content += "#frame:duration x y" + "\n";
            for (int j=0;j<anim_frame_list.size();j++) {
                if (anim_frame_list.get(j).animation.equals(a))
                    content += "frame:" + anim_frame_list.get(j).dur + " " + anim_frame_list.get(j).x + " " + anim_frame_list.get(j).y + "\n";
            }
            content += "\n\n";
        }
        
        return content;
    }
    
    public void writeToFile (String path) throws IOException {
        BufferedWriter bw = new BufferedWriter (new FileWriter (new File (path)));
        bw.write(getWritten());
        bw.flush();
        bw.close();
    }
    
    public void writeToFile () {
        try {
            writeToFile (this.actor_file);
        } catch (IOException e) {
            Log.log(Log.ACTOR,Log.LogLevel.ERROR,"while trying to save actor '"+this.actor_name+"' to file '"+this.actor_file+"'");
        }
    }
    
    public void render (int x, int y, Color filter, String anim) {
        if (anims.containsKey(anim)) {
            if (filter == null) {
                anims.get(anim).draw(x, y);
            } else {
                anims.get(anim).draw(x, y, filter);
            }
        }
    }
}
