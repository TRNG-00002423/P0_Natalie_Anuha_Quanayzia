package com.project0.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Path to the db
    private static final String URL;

    static {
        Path dbPath = Paths.get(
                System.getProperty("user.dir"),
                "..",
                "RevatureExpenseManager",
                "db.sqlite3"
        ).normalize();

        URL = "jdbc:sqlite:" + dbPath;
    }
    private static Connection connection=null;

    //Singleton - one connection for the apps lifetime
    public static Connection getConnection() throws SQLException{
        if (connection==null || connection.isClosed()){
            connection= DriverManager.getConnection(URL);
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
