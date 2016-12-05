/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.logger;

import engine.environment.Settings;
import static engine.logger.Log.log;

/**
 * @author Emil Simon
 */

public class LogType {
    public String prefix;
    public String log_file;
    public boolean debug;

    public LogType (String pre, String file) {
        prefix = pre;
        log_file = file;
        Log.createLog(file);
        log(this, "opened new log file");
    }
};
