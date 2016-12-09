/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.logger.Log;
import engine.environment.Settings;
import engine.game.actors.Actor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.newdawn.slick.Color;

/**
 * @author Emil Simon
 */

public class EntityType {
    public String entity_type_name;
    public String entity_type_file;
    public String actor_name;
    public HashMap<String, EntityVar> vars;
    public Collider collider;
    public int originX, originY;
    public Color filter; 
    
    public EntityType (String name, String entityFilePath) throws FileNotFoundException, IOException {
        entity_type_name = name;
        entity_type_file = entityFilePath;
        actor_name = "";
        vars = new HashMap <> ();
        collider = new Collider ();
        filter = Color.white;
        
        //try to open and read settings
        BufferedReader br = new BufferedReader (new FileReader (entityFilePath));
        String line;

        while ((line=br.readLine ())!=null) {
            if (!line.startsWith("#") && !line.isEmpty()) { // COMMENT
                String[] words = line.trim().split(":");
                switch (words[0].trim()) {
                    case "actor" :
                        actor_name = words[1].trim();
                        break;

                    case "color" :
                        String[] rgba = words[1].trim().split(" ");
                        if (rgba.length == 4)
                            filter = new Color (Float.parseFloat(rgba[0]), Float.parseFloat(rgba[1]), Float.parseFloat(rgba[2]), Float.parseFloat(rgba[3]));
                        else
                            Log.err(Log.ENTITY,"found wrong number of arguments for color filter!",null);
                        break;

                    case "collider" :
                        if (!collider.setState(words[1].trim())) {
                            Log.err(Log.ENTITY,"unrecognized collider state '"+words[1].trim()+"'",null);
                        }
                        break;

                    case "collider_height" :
                        collider.height_layer = Integer.parseInt(words[1].trim());
                        break;

                    case "box_collider" :
                        String[] box = words[1].trim().split(" ");
                        if (box.length == 2) {
                            collider.box_width = Integer.parseInt(box[0].trim());
                            collider.box_height = Integer.parseInt(box[1].trim());
                        } else
                            Log.err(Log.ENTITY,"found wrong number of arguments for box collider!",null);
                        break;

                    case "rad_collider" :
                        collider.radius = Integer.parseInt(words[1].trim());
                        break;

                    case "origin" :
                        String[] originArgs = words[1].trim().split(" ");
                        if (originArgs.length == 2) {
                            this.originX = Integer.parseInt(originArgs[0].trim());
                            this.originY = Integer.parseInt(originArgs[1].trim());
                        } else
                            Log.err(Log.ENTITY,"found wrong number of arguments for origin point!",null);
                        break;

                    case "var" :
                        String[] var_properties = words[1].trim().split(" ");
                        String derived_value = "";
                        
                        switch (EntityVar.getVarType(var_properties[0])) {
                            case INTEGER :
                                derived_value = var_properties[2].trim();
                                break;
                            case REAL :
                                derived_value = var_properties[2].trim();
                                break;
                            case TEXT :
                                derived_value = words[1].substring(var_properties[0].length() + var_properties[1].length() + 2);
                                break;
                            case BOOL :
                                derived_value = var_properties[2].trim();
                                break;
                            case MAP :
                                derived_value = var_properties[2].trim();
                                break;
                            case TRIGGER :
                                derived_value = var_properties[2].trim();
                                break;
                            case ENTITY_TYPE :
                                derived_value = var_properties[2].trim();
                                break;
                            case ENTITY :
                                derived_value = var_properties[2].trim();
                                break;
                            case ACTOR :
                                derived_value = var_properties[2].trim();
                                break;
                            case NULL :
                                derived_value = var_properties[2].trim();
                                break;
                            default :
                                break;
                        }
                        
                        setVar (var_properties[1], derived_value, EntityVar.getVarType(var_properties[0]));
                        break;

                    default :
                        break;
                }
            }
        }
    }
     
    public EntityType (String entityName) throws IOException {
        entity_type_name = entityName;
        entity_type_file = Consts.ENTITY_DUMP_FOLDER + entity_type_name + "." + Consts.ENTITY_FILE_EXTENSION;
        actor_name = "";
        vars = new HashMap <> ();
        collider = new Collider ();
        filter = Color.white;
        
        File f = new File (entity_type_file);
        if (!f.exists()) {
            f.createNewFile();
        }
    }
    
    
     
    public EntityVar[] getVars () {
        EntityVar[] result = new EntityVar [vars.size()];
        for (int i=0;i<vars.size();i++) {
            result[i] = vars.get(vars.keySet().toArray()[i].toString());
        }
        return result;
    }
    public final void setVar (String name, String value, EntityVar.EntityVarType type) {
        if (!EntityVar.isVarValueLegit (value,type)) {
            if (Settings.debug_entities)
                Log.log(Log.ENTITY,type.toString()+" variable '"+name+"' has invalid value: '"+value+"'");
            return;
        }
        
        if (vars.containsKey(name)) {
            vars.get(name).type = type;
            vars.get(name).value = value;
            if (Settings.debug_entities)
                Log.log(Log.ENTITY,"set variable '"+name+"' to value: ("+type.toString()+") '"+value+"'");
        } else {
            vars.put(name, new EntityVar (name,value,type));
            if (Settings.debug_entities)
                Log.log(Log.ENTITY,"added variable '"+name+"' with value: ("+type.toString()+") '"+value+"'");
        }
    }
    
    
    
    public Actor getActor () {
        return ResMgr.getActor(actor_name);
    }
    
    
    
    public String getWritten () {
        String content = "";
        content += "#"+entity_type_name+" entity file:" + "\n\n\n";
        
        content += "#actor:name" + "\n";
        content += "actor:"+actor_name + "\n\n\n";
        
        content += "#color:r g b a" + "\n";
        content += "color:"+filter.r+" "+filter.g+" "+filter.b+" "+filter.a + "\n\n\n";
        
        content += "#collider:box/radial/none" + "\n";
        content += "collider:"+collider.state.toString().toLowerCase() + "\n\n";
        content += "#collider_height:height" + "\n";
        content += "collider_height:"+collider.height_layer + "\n\n";
        content += "#FOR BOX COLLIDERS" + "\n";
        content += "#box_collider:width height" + "\n";
        content += "box_collider:"+collider.box_width+" "+collider.box_height + "\n\n";
        content += "#FOR RADIAL COLLIDERS" + "\n";
        content += "#rad_collider:radius" + "\n";
        content += "rad_collider:"+collider.radius + "\n\n";
        
        content += "#origin:x y" + "\n";
        content += "origin:"+originX+" "+originY + "\n\n";
        
        content += "#variables" + "\n";
        content += "#  default values for variables on entity instantiation" + "\n";
        content += "#var:var_type var_name var_value" + "\n";
        String[] varNames = vars.keySet().toArray(new String [vars.size()]);
        for (int i=0;i<varNames.length;i++) {
            String whichVar = varNames[i];
            content += "var:"+vars.get(whichVar).type.toString().toLowerCase()+" "+vars.get(whichVar).name+" "+vars.get(whichVar).value+" "+ "\n";
        }
        content += "\n\n";
        
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
            writeToFile (this.entity_type_file);
        } catch (IOException ex) {
            Log.err(Log.ENTITY,"while trying to save entity type '"+this.entity_type_name+"' to file '"+this.entity_type_file+"'",ex);
        }
    }
}
