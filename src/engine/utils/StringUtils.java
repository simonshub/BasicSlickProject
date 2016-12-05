/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.utils;

/**
 * @author Emil Simon
 */

public abstract class StringUtils {
    
    public static final String capitalizeFirstChar (String str) {
       return Character.toString(str.charAt(0)).toUpperCase() + str.substring(1);
    }
    
}
