/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.environment.Consts;
import engine.environment.Data;
import engine.environment.ResMgr;
import engine.game.maps.Camera;
import engine.utils.Location;
import engine.utils.Vector;
import engine.logger.Log;
import engine.environment.Settings;
import engine.game.actors.Actor;
import engine.game.actors.AnimatedSprite;
import engine.game.triggers.TriggerEvent;
import java.util.HashMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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
    public String behaviour;
    
    private float x_falloff, y_falloff;
    
    
    
    public Entity (EntityType type, int counter, int locX, int locY) {
        x_falloff = 0;
        y_falloff = 0;
        
        this.parent_type = type;
        this.name = this.parent_type.entity_type_name + "_" + String.format("%06d", counter);
        this.vars = new HashMap <> (type.vars);
        this.actor = ResMgr.getActor(type.actor_name);
        this.location = new Location (locX,locY);
        this.result_force = new Vector (0,0);
        this.current_anim = ResMgr.getActor(parent_type.actor_name).default_anim;
    }

    public Entity (Entity parent, int counter) {
        x_falloff = 0;
        y_falloff = 0;
        
        this.parent_type = parent.parent_type;
        this.name = parent_type.entity_type_name + "_" + String.format("%06d", counter);
        this.vars = new HashMap <> (parent.vars);
        this.actor = parent.actor;
        this.location = parent.location;
        this.result_force = parent.result_force;
        this.current_anim = ResMgr.getActor(parent_type.actor_name).default_anim;
    }
    
    public Entity (String[] lines) {
        x_falloff = 0;
        y_falloff = 0;
        
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
                        
                    case "behaviour" :
                        behaviour = words[1].trim();

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
        
        if (parent_type!=null) {
            this.actor = ResMgr.getActor(parent_type.actor_name);
            this.current_anim = actor.default_anim;
        } else {
            Log.err(Log.ENTITY, "while creating entity '"+name+"' - no defined parent type!", null);
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
    public final EntityVar getVar (String name) {
        return vars.get(name);
    }
    
    public void playAnim (String anim, boolean one_off) {
        this.current_anim = anim;
        //if (one_off)
            
    }
    
   
    
    public void render (GameContainer gc, StateBasedGame sbg, Graphics g, Camera c) {
        this.renderWithFilter(gc, sbg, g, c, filter);
    }
    
    public void renderWithFilter (GameContainer gc, StateBasedGame sbg, Graphics g, Camera c, Color filter) {
        if (this.location.isInBoundsWithDiff(c.location, c.getLowerRight(), Consts.ENTITY_OFFSCREEN_DRAW_MARGIN)) {
            int trans_x = location.x - c.location.x - parent_type.originX;
            int trans_y = location.y - c.location.y - parent_type.originY;
            actor.render((int)(trans_x*c.zoom), (int)(trans_y*c.zoom), filter, current_anim, c.zoom);
        }
    }
    
    public void renderDebug (GameContainer gc, StateBasedGame sbg, Graphics g, Camera c, Color sprite_bound_filter) {
        AnimatedSprite sprite = ResMgr.getAnimatedSprite(actor.sheet);

        g.setColor(sprite_bound_filter);
//        g.drawRect(location.x-sprite.dimX/2, location.y-sprite.dimY/2, sprite.dimX, sprite.dimY);
        g.drawRect(location.x - c.location.x - parent_type.originX, location.y - c.location.y - parent_type.originY, sprite.dimX, sprite.dimY);
        parent_type.collider.render(g, location, c, new Color (1f,0f,0f,0.5f));
        
        g.setColor(Color.white);
        g.fillOval(location.x - c.location.x - 1,
                   location.y - c.location.y - 1,
                   2, 2);
    }
    
    public boolean move (float d_x, float d_y) {
        x_falloff += d_x % 1;
        y_falloff += d_y % 1;
        
        if ((x_falloff >= 1.0f) || (x_falloff <= -1.0f)) {
            d_x += (int) x_falloff;
            x_falloff -= (int) x_falloff;
        }
        if ((y_falloff >= 1.0f) || (y_falloff <= -1.0f)) {
            d_y += (int) y_falloff;
            y_falloff -= (int) y_falloff;
        }
        
        return this.moveWithCollisionDetection(Data.currentMap.entities, (int)d_x, (int)d_y);
    }
    
    public boolean moveWithCollisionDetection (Entity[] entity_list, int d_x, int d_y) {
        Location old_loc = location;
        location = location.offset(d_x,d_y);
        for (Entity e : entity_list) {
            if (e.intersectsCollider(this)) {
                location = old_loc;
                return false;
            }
        }
        return true;
    }
    public boolean moveWithCollisionDetection (HashMap<String,Entity> entity_list, int d_x, int d_y) {
        Location old_loc = location;
        location = location.offset(d_x,d_y);
        for (Entity e : entity_list.values()) {
            if (e.name.equals(this.name)) continue;
            if (e.intersectsCollider(this)) {
                location = old_loc;
                return false;
            }
        }
        return true;
    }
    
    public boolean moveToWithCollisionDetection (Entity[] entity_list, int x, int y) {
        Location old_loc = location;
        location = new Location (x,y);
        for (Entity e : entity_list) {
            if (e.intersectsCollider(this)) {
                location = old_loc;
                return false;
            }
        }
        return true;
    }
    public boolean moveToWithCollisionDetection (HashMap<String,Entity> entity_list, int x, int y) {
        Location old_loc = location;
        location = new Location (x,y);
        for (Entity e : entity_list.values()) {
            if (e.intersectsCollider(this)) {
                location = old_loc;
                return false;
            }
        }
        return true;
    }

    
    
    public void update(GameContainer gc, StateBasedGame sbg, int i) {
        if (ResMgr.hasTrigger(behaviour)) {
            ResMgr.getTrigger(behaviour).run(true, new TriggerEvent ("behaviour"));
        }
    }
    
    
    
    public String getWritten (String prefix) {
        String content = "";
        content += prefix + "#"+name+" entity :" + "\n\n";
        
        content += prefix + "#name:entity_name" + "\n";
        content += prefix + "name:"+name + "\n\n";
        
        content += prefix + "#type:entity_type_name" + "\n";
        content += prefix + "type:"+parent_type.entity_type_name + "\n\n";
        
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
    
    
    
    public boolean isPointInside (Location loc) {
        Collider c = parent_type.collider;
        switch (c.state) {
            case RADIAL :
                return loc.isInRange(this.location, c.radius);
            case BOX :
                return loc.isInBounds(this.location.offset(-c.box_width/2,-c.box_height/2),
                                      this.location.offset( c.box_width/2, c.box_height/2));
            case NONE :
                AnimatedSprite s = ResMgr.getAnimatedSprite(parent_type.getActor().sheet);
                return loc.isInBounds(this.location.offset(-s.dimX/2,-s.dimY/2),
                                      this.location.offset( s.dimX/2, s.dimY/2));
            default :
                break;
        }
        
        return false;
    }
    public boolean isPointInside (int x, int y) {
        Location loc = new Location (x,y);
        Collider c = parent_type.collider;
        switch (c.state) {
            case RADIAL :
                return loc.isInRange(this.location, c.radius);
            case BOX :
                return loc.isInBounds(this.location.offset(-c.box_width/2,-c.box_height/2),
                                      this.location.offset( c.box_width/2, c.box_height/2));
            default :
                break;
        }
        
        return false;
    }
    
    public boolean intersectsCollider (Entity e) {
        Collider t_c = parent_type.collider;
        Collider e_c = e.parent_type.collider;
        
        if (t_c.state == Collider.ColliderState.BOX) {
            if (e_c.state == Collider.ColliderState.BOX) {
                return isPointInside(e.location.offset(-e_c.box_width/2,-e_c.box_height/2)) ||
                       isPointInside(e.location.offset(-e_c.box_width/2, e_c.box_height/2)) ||
                       isPointInside(e.location.offset( e_c.box_width/2,-e_c.box_height/2)) ||
                       isPointInside(e.location.offset( e_c.box_width/2, e_c.box_height/2));
            } else if (e_c.state == Collider.ColliderState.RADIAL) {
                return location.offset(-t_c.box_width/2,-t_c.box_height/2).isInRange(e.location, e_c.radius) ||
                       location.offset(-t_c.box_width/2, t_c.box_height/2).isInRange(e.location, e_c.radius) ||
                       location.offset( t_c.box_width/2,-t_c.box_height/2).isInRange(e.location, e_c.radius) ||
                       location.offset( t_c.box_width/2, t_c.box_height/2).isInRange(e.location, e_c.radius) ||
                       location.isInRange(e.location, e_c.radius+t_c.box_height/2) ||
                       location.isInRange(e.location, e_c.radius+t_c.box_width/2);
            }
        } else if (t_c.state == Collider.ColliderState.RADIAL) {
            if (e_c.state == Collider.ColliderState.BOX) {
                return e.location.offset(-e_c.box_width/2,-e_c.box_height/2).isInRange(location, t_c.radius) ||
                       e.location.offset(-e_c.box_width/2, e_c.box_height/2).isInRange(location, t_c.radius) ||
                       e.location.offset( e_c.box_width/2,-e_c.box_height/2).isInRange(location, t_c.radius) ||
                       e.location.offset( e_c.box_width/2, e_c.box_height/2).isInRange(location, t_c.radius) ||
                       e.location.offset( e_c.box_width/2,-e_c.box_height/2).isInRange(location, t_c.radius) ||
                       e.location.isInRange(location, t_c.radius+e_c.box_height/2) ||
                       e.location.isInRange(location, t_c.radius+e_c.box_width/2);
            } else if (e_c.state == Collider.ColliderState.RADIAL) {
                return location.isInRange(e.location, t_c.radius+e_c.radius);
            }
        }
        
        return false;
    }
    public boolean intersectsCollider (EntityType e_t, int x, int y) {
        Collider t_c = parent_type.collider;
        Collider e_c = e_t.collider;
        Location loc = new Location (x,y);
        
        if (t_c.state == Collider.ColliderState.BOX) {
            if (e_c.state == Collider.ColliderState.BOX) {
                return isPointInside(loc.offset(-e_c.box_width/2,-e_c.box_height/2)) ||
                       isPointInside(loc.offset(-e_c.box_width/2, e_c.box_height/2)) ||
                       isPointInside(loc.offset( e_c.box_width/2,-e_c.box_height/2)) ||
                       isPointInside(loc.offset( e_c.box_width/2, e_c.box_height/2));
            } else if (e_c.state == Collider.ColliderState.RADIAL) {
                return location.offset(-t_c.box_width/2,-t_c.box_height/2).isInRange(loc, e_c.radius) ||
                       location.offset(-t_c.box_width/2, t_c.box_height/2).isInRange(loc, e_c.radius) ||
                       location.offset( t_c.box_width/2,-t_c.box_height/2).isInRange(loc, e_c.radius) ||
                       location.offset( t_c.box_width/2, t_c.box_height/2).isInRange(loc, e_c.radius) ||
                       location.isInRange(loc, e_c.radius+t_c.box_height/2) ||
                       location.isInRange(loc, e_c.radius+t_c.box_width/2);
            }
        } else if (t_c.state == Collider.ColliderState.RADIAL) {
            if (e_c.state == Collider.ColliderState.BOX) {
                return loc.offset(-e_c.box_width/2,-e_c.box_height/2).isInRange(location, t_c.radius) ||
                       loc.offset(-e_c.box_width/2, e_c.box_height/2).isInRange(location, t_c.radius) ||
                       loc.offset( e_c.box_width/2,-e_c.box_height/2).isInRange(location, t_c.radius) ||
                       loc.offset( e_c.box_width/2, e_c.box_height/2).isInRange(location, t_c.radius) ||
                       loc.isInRange(location, t_c.radius+e_c.box_height/2) ||
                       loc.isInRange(location, t_c.radius+e_c.box_width/2);
            } else if (e_c.state == Collider.ColliderState.RADIAL) {
                return location.isInRange(loc, t_c.radius+e_c.radius);
            }
        }
        
        return false;
    }
    
    
    
    @Override
    public int compareTo(Entity other_entity) {
        return ((this.location.y - this.parent_type.originY) - (other_entity.location.y - other_entity.parent_type.originY));
    }
}
