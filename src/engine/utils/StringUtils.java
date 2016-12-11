/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Emil Simon
 */

public abstract class StringUtils {
    
    public static final String capitalizeFirstChar (String str) {
       return Character.toString(str.charAt(0)).toUpperCase() + str.substring(1);
    }
    
    
    
    public static final String[] listToArray (List<String> list) {
        return Arrays.copyOf(list.toArray(), list.size(), String[].class);
    }
    
}
