package com.project0.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // Path to the db
    private static final String URL;

    static {
    String userDir = System.getProperty("user.dir");
    Path dbPath;

    // Check if we are already running from the root project folder
    if (userDir.endsWith("P0_Natalie_Anuha_Quanayzia")) {
        dbPath = Paths.get(userDir, "RevatureExpenseManager", "db.sqlite3").normalize();
    } else {
        // Fallback for when running tests locally from inside /manager-app
        dbPath = Paths.get(userDir, "..", "RevatureExpenseManager", "db.sqlite3").normalize();
    }

    URL = "jdbc:sqlite:" + dbPath.toString();
}
    private static Connection connection=null;

    //Singleton - one connection for the apps lifetime
    public static Connection getConnection() throws SQLException{
        if (connection==null || connection.isClosed()){
            connection= DriverManager.getConnection(URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return connection;
    }

    public static void closeConnection(){
        try{
            if(connection !=null && !connection.isClosed()){
                connection.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


}
