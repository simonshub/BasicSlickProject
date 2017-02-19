/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Emil Simon
 */

public abstract class StringUtils {
    
    public static final String capitalizeFirstChar (String str) {
        if (str.isEmpty())
            return "";
        return Character.toString(str.charAt(0)).toUpperCase() + str.substring(1);
    }
    
    
    
    public static final String[] listToArray (List<String> list) {
        return Arrays.copyOf(list.toArray(), list.size(), String[].class);
    }
    
    public static final String[] removeEmpty (String... list) {
        List<String> result = new ArrayList<> ();
        for (String s : list) {
            if (!s.isEmpty())
                result.add(s);
        }
        return result.toArray(new String [result.size()]);
    }
    
    public static final String[] trimAll (String... list) {
        String[] result = new String [list.length];
        for (int i=0;i<list.length;i++) {
            result[i] = list[i].trim();
        }
        return result;
    }
    
    public static final String concatLinesFromList (List<String> lines) {
        String res = "";
        for (String line : lines) {
            res += line + "\n";
        }
        return res;
    }
    
    public static final String concatLinesFromArray (String[] lines) {
        String res = "";
        for (String line : lines) {
            res += line + "\n";
        }
        return res;
    }
    
}
