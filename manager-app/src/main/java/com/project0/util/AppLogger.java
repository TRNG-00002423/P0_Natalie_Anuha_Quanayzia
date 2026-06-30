package com.project0.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppLogger {

    // One shared logger for the whole app
    private static final Logger logger = Logger.getLogger("ExpenseManager");
    private static boolean initialized = false;

    public static Logger getLogger() {
        if (!initialized) {
            try {
                // true = append to the file instead of overwriting it each run
                FileHandler fileHandler = new FileHandler("manager-app.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
                // Don't also dump log lines into the console menu
                logger.setUseParentHandlers(false);
                initialized = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }
}
