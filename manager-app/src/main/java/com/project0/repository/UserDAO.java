package com.project0.repository;


import com.project0.model.Users;

import java.util.List;

public interface UserDAO {

    Users getUserByUsername(String username);
    List<Users> getAllEmployees();
    List<Users> getAllUsers();
}
