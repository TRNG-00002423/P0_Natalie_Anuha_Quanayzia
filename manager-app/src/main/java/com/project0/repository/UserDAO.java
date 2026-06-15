package com.project0.repository;


import com.project0.model.Users;

public interface UserDAO {

    Users getUserByUsername(String username);
}
