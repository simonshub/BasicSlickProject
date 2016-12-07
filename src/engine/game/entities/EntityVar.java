/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.game.entities;

import engine.logger.Log;
import engine.environment.ResMgr;

/**
 * @author Emil Simon
 */

public class EntityVar {
    public enum EntityVarType { INTEGER, REAL, TEXT, BOOL, MAP, TRIGGER, ENTITY_TYPE, ENTITY, ACTOR, NULL };
    public String name;
    public String value;
    public EntityVarType type;
     
    public static boolean isVarValueLegit (String value, EntityVarType type) {
        try {
            switch (type) {
                case INTEGER :
                    Integer.parseInt(value);
                    break;
                case REAL :
                    Double.parseDouble(value);
                    break;
                case TEXT :
                    return true;
                case BOOL :
                    return (value.equals("false") || value.equals("true") || value.equals("0") || value.equals("1"));
                case MAP :
                    break;
                case TRIGGER :
                    return (ResMgr.trigger_lib.containsKey(value));
                case ENTITY_TYPE :
                    break;
                case ENTITY :
                    break;
                case ACTOR :
                    break;
                default :
                    return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
     
    public static EntityVarType getVarType (String type) {
        type = type.toLowerCase().trim();
        EntityVarType result = EntityVarType.NULL;

        if (type.equals("int") || type.equals("integer")) {
            result = EntityVarType.INTEGER;
        } else if (type.equals("real")) {
            result = EntityVarType.REAL;
        } else if (type.equals("text") || type.equals("string")) {
            result = EntityVarType.TEXT;
        } else if (type.equals("bool") || type.equals("boolean")) {
            result = EntityVarType.BOOL;
        } else if (type.equals("map")) {
            result = EntityVarType.MAP;
        } else if (type.equals("trig") || type.equals("trigger")) {
            result = EntityVarType.TRIGGER;
        } else if (type.equals("entity_type") || type.equals("ent_type")) {
            result = EntityVarType.ENTITY_TYPE;
        } else if (type.equals("ent") || type.equals("entity")) {
            result = EntityVarType.ENTITY;
        } else if (type.equals("act") || type.equals("actor")) {
            result = EntityVarType.ACTOR;
        } else if (type.equals("null") || type.equals("none")) {
            result = EntityVarType.NULL;
        } else {
            Log.log(Log.ENTITY,Log.LogLevel.ERROR,"unknown variable type '"+type+"'");
        }
        
        return result;
    }
    
    public EntityVar (String name, String value, EntityVarType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
}
