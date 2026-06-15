package com.project0.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Path to the db
    private static final String URL="jdbc:sqlite:database/expense_manager.db";
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
                connection.isClosed();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


}
