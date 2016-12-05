/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.game.maps.Camera;
import engine.utils.Location;
import engine.utils.Vector;
import engine.logger.Log;
import engine.environment.ResMgr;
import engine.environment.Settings;
import java.util.HashMap;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Emil Simon
 */

public class Entity {
    public String name;
    public String type;
    public HashMap<String, EntityVar> vars;
    public Location location;
    public Vector result_force;
    
    
    
    public Entity (EntityType type, int counter, int locX, int locY) {
        this.type = type.entity_type_name;
        this.name = this.type + "_" + counter;
        this.vars = new HashMap <> (type.vars);
        this.location = new Location (locX,locY);
        this.result_force = new Vector (0,0);
    }

    public Entity (Entity parent, int counter) {
        this.type = parent.type;
        this.name = type + "_" + counter;
        this.vars = new HashMap <> (parent.vars);
        this.location = parent.location;
        this.result_force = parent.result_force;
    }
    
    public Entity (String[] lines) {
        this.name = "";
        this.vars = new HashMap <> ();
        this.location = new Location (0,0);
        this.result_force = new Vector (0,0);

        for (String line : lines) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                // COMMENT
                String[] words = line.trim().split(":");
                switch (words[0].trim()) {
                    case "name" :
                        name = words[1].trim();
                        break;
                        
                    case "type" :
                        type = words[1].trim();
                        break;
                        
                    case "location" :
                        location.fromString(words[1]);
                        break;
                        
                    case "force" :
                        result_force.fromString(words[1]);
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
    
   
    
    public void render (GameContainer gc, StateBasedGame sbg, Graphics g, Camera c) {
    }

    
    
    public void update(GameContainer gc, StateBasedGame sbg, int i) {
    }
    
    
    
    public String getWritten (String prefix) {
        String content = "";
        content += prefix + "#"+name+" entity :" + "\n\n";
        
        content += prefix + "#name:entity_name" + "\n";
        content += prefix + "name:"+name + "\n\n";
        
        content += prefix + "#type:entity_type_name" + "\n";
        content += prefix + "type:"+name + "\n\n";
        
        content += prefix + "#location:x,y" + "\n";
        content += prefix + "location:"+location.toString() + "\n\n";
        
        content += prefix + "#force:x,y" + "\n";
        content += prefix + "force:"+result_force.toString() + "\n\n";
        
        content += prefix + "#variables" + "\n";
        content += prefix + "#  default values for variables on entity instantiation" + "\n";
        content += prefix + "#var:var_type var_name var_value" + "\n";
        String[] varNames = vars.keySet().toArray(new String [vars.size()]);
        for (int i=0;i<varNames.length;i++) {
            String whichVar = varNames[i];
            content += prefix + "var:"+vars.get(whichVar).type.toString().toLowerCase()+" "+vars.get(whichVar).name+" "+vars.get(whichVar).value+" "+ "\n";
        }
        content += "\n";
        
        return content;
    }
}
