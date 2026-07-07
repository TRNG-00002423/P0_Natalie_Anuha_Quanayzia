package com.project0.services;

import com.project0.exceptions.AuthenticationException;
import com.project0.exceptions.UserNotFoundException;
import com.project0.model.Users;
import com.project0.repository.UserDAO;
import com.project0.repository.UserDAOImpl;
import com.project0.util.AppLogger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.logging.Logger;

public class AuthService {

    private static final Logger logger = AppLogger.getLogger();

    UserDAO ud;

    public AuthService() {
        this.ud = new UserDAOImpl();
    }

    public Users login(String username, String password) {

        Users user = ud.getUserByUsername(username);

        if (user == null) {
            logger.warning("Login failed: unknown user '" + username + "'");
            throw new UserNotFoundException("Username or password is incorrect");
        }

        String storedPassword = user.getPassword().trim().replace("$2b$", "$2a$");

        String cleanPassword = password != null ? password.trim() : "";

        if (!BCrypt.checkpw(cleanPassword, storedPassword)) {
            logger.warning("Login failed: wrong password for user '" + username + "'");
            throw new AuthenticationException("Username or password is incorrect");
        }

        if (!"manager".equalsIgnoreCase(user.getRole())) {
            logger.warning("Login denied: user '" + username + "' is not a manager");
            throw new AuthenticationException("This account is not a manager account.");
        }

        logger.info("Login success: user '" + username + "'");
        return user;
    }

    public List<Users> getAllEmployees() {
        return ud.getAllEmployees();
    }
}
