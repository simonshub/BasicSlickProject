/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.utils;

import engine.environment.Consts;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Emil Simon
 */

public abstract class FileUtils {
    
    public static final List<File> getAllFiles(String directoryName) {
        List<File> files = new ArrayList<> ();
        getAllFiles(directoryName,files);
        return files;
    }
    
    public static final void getAllFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);
        
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                getAllFiles(file.getAbsolutePath(), files);
            }
        }
    }
    
    
    
    public static final String getExtension (String filepath) {
        return filepath.substring(filepath.lastIndexOf(".")+1);
    }
    
    public static final String getNameWithoutExtension (String filename) {
        return filename.substring(0,filename.lastIndexOf("."));
    }
    
    
    
    public static final String getTriggerPath (File f) {
        String path = f.getPath().replace("\\","/");
        return path.substring(path.lastIndexOf(Consts.trigger_dump_folder));
    }
    
    public static final String getTriggerName (File f) {
        return getNameWithoutExtension(getTriggerPath(f).substring(Consts.trigger_dump_folder.length()));
    }
    
}
