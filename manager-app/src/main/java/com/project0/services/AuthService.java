package com.project0.services;

import com.project0.exceptions.AuthenticationException;
import com.project0.exceptions.UserNotFoundException;
import com.project0.model.Users;
import com.project0.repository.UserDAO;
import com.project0.repository.UserDAOImpl;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    UserDAO ud;

    public AuthService() {
        this.ud = new UserDAOImpl();
    }

   public Users login(String username, String password) {

    Users user = ud.getUserByUsername(username);

    if (user == null) {
        throw new UserNotFoundException("Username or password is incorrect");
    }

    String storedPassword = user.getPassword().replace("$2b$", "$2a$");
    if (!BCrypt.checkpw(password, storedPassword)) {
        throw new AuthenticationException("Username or password is incorrect");
    }

    if (!"manager".equalsIgnoreCase(user.getRole())) {
        throw new AuthenticationException("This account is not a manager account.");
    }

    return user;
}
}  