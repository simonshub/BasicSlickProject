/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.environment.Consts;
import engine.environment.ResMgr;
import engine.game.maps.Camera;
import engine.utils.Location;
import engine.utils.Vector;
import engine.logger.Log;
import engine.environment.Settings;
import engine.game.actors.Actor;
import engine.game.actors.AnimatedSprite;
import java.util.HashMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

/**
 * @author Emil Simon
 */

public class Entity implements Comparable<Entity> {
    public String name;
    public EntityType parent_type;
    public String current_anim;
    public Actor actor;
    public Color filter;
    public HashMap<String, EntityVar> vars;
    public Location location;
    public Vector result_force;
    
    
    
    public Entity (EntityType type, int counter, int locX, int locY) {
        this.parent_type = type;
        this.name = this.parent_type.entity_type_name + "_" + String.format("%06d", counter);
        this.vars = new HashMap <> (type.vars);
        this.actor = ResMgr.getActor(type.actor_name);
        this.location = new Location (locX,locY);
        this.result_force = new Vector (0,0);
        this.current_anim = ResMgr.getActor(parent_type.actor_name).default_anim;
    }

    public Entity (Entity parent, int counter) {
        this.parent_type = parent.parent_type;
        this.name = parent_type.entity_type_name + "_" + String.format("%06d", counter);
        this.vars = new HashMap <> (parent.vars);
        this.actor = parent.actor;
        this.location = parent.location;
        this.result_force = parent.result_force;
        this.current_anim = ResMgr.getActor(parent_type.actor_name).default_anim;
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
                        parent_type = ResMgr.hasEntityType(words[1].trim()) ? ResMgr.getEntityType(words[1].trim()) : null;
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
        
        this.actor = ResMgr.getActor(parent_type.actor_name);
        this.current_anim = actor.default_anim;
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
        if (this.location.isInBoundsWithDiff(c.location, c.getLowerRight(), Consts.ENTITY_OFFSCREEN_DRAW_MARGIN)) {
            int trans_x = location.x - c.location.x - parent_type.originX;
            int trans_y = location.y - c.location.y - parent_type.originY;
            actor.render(trans_x, trans_y, filter, current_anim);
        }
    }
    
    public void renderDebug (GameContainer gc, StateBasedGame sbg, Graphics g, Camera c) {
        AnimatedSprite sprite = ResMgr.getAnimatedSprite(actor.sheet);

        g.setColor(new Color (255,255,255,150));
//        g.drawRect(location.x-sprite.dimX/2, location.y-sprite.dimY/2, sprite.dimX, sprite.dimY);
        g.drawRect(location.x - c.location.x - parent_type.originX, location.y - c.location.y - parent_type.originY, sprite.dimX, sprite.dimY);
        
        g.setColor(new Color (255,0,0,100));
        if (parent_type.collider.state == Collider.ColliderState.BOX) {
            g.drawRect(location.x - c.location.x - parent_type.collider.box_width/2,
                       location.y - c.location.y - parent_type.collider.box_height/2,
                       parent_type.collider.box_width, parent_type.collider.box_height);
            g.fillRect(location.x - c.location.x - parent_type.collider.box_width/2,
                       location.y - c.location.y - parent_type.collider.box_height/2,
                       parent_type.collider.box_width, parent_type.collider.box_height);
        } else if (parent_type.collider.state == Collider.ColliderState.RADIAL) {
            g.drawOval(location.x - c.location.x - parent_type.collider.radius,
                       location.y - c.location.y - parent_type.collider.radius,
                       parent_type.collider.radius*2, parent_type.collider.radius*2);
            g.fillOval(location.x - c.location.x - parent_type.collider.radius,
                       location.y - c.location.y - parent_type.collider.radius,
                       parent_type.collider.radius*2, parent_type.collider.radius*2);
        }
        g.setColor(new Color (255,255,255,255));
        g.fillOval(location.x - c.location.x - 1,
                   location.y - c.location.y - 1,
                   2, 2);
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
        for (String whichVar : varNames) {
            content += prefix + "var:"+vars.get(whichVar).type.toString().toLowerCase()+" "+vars.get(whichVar).name+" "+vars.get(whichVar).value+" "+ "\n";
        }
        content += "\n";
        
        return content;
    }
    
    
    
    @Override
    public int compareTo(Entity other_entity) {
        return ((this.location.y - this.parent_type.originY) - (other_entity.location.y - other_entity.parent_type.originY));
    }
}
