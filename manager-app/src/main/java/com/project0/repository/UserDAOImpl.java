package com.project0.repository;

import com.project0.model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {


    @Override
    public Users getUserByUsername(String username) {
        String sqlSelect="SELECT * FROM users WHERE username=?";
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
           e.printStackTrace();
       }

        return null;
    }
}
