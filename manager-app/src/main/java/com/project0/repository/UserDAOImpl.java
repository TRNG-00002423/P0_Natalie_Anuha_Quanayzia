package com.project0.repository;

import com.project0.model.Users;
import com.project0.util.DatabaseConnection;
import com.project0.util.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {

    private static final Logger logger = AppLogger.getLogger();


    @Override
    public Users getUserByUsername(String username) {
        String sqlSelect="SELECT * FROM ExpenseManager_user WHERE username=?";
       try(Connection conn = DatabaseConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sqlSelect)){

           stmt.setString(1, username);
           ResultSet rs = stmt.executeQuery();

           if(rs.next()){
               return new Users(
                       rs.getInt("id"),
                       rs.getString("username"),
                       rs.getString("password"),
                       rs.getString("role")

               );
           }



       }catch(SQLException e){
           logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
       }

        return null;
    }

    @Override
    public List<Users> getAllEmployees() {
        String sqlSelect = "SELECT * FROM ExpenseManager_user WHERE LOWER(role) = 'employee' ORDER BY id";
        List<Users> employees = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employees.add(new Users(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return employees;
    }
}
