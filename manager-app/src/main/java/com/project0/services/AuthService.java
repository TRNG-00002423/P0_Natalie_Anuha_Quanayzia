package com.project0.services;

import com.project0.exceptions.AuthenticationException;
import com.project0.exceptions.UserNotFoundException;
import com.project0.model.Users;
import com.project0.repository.UserDAO;
import com.project0.repository.UserDAOImpl;
import com.project0.util.AppLogger;

import java.util.logging.Logger;

public class AuthService {

    private static final Logger logger = AppLogger.getLogger();

    UserDAO ud;

    public AuthService() {
        this.ud = new UserDAOImpl();
    }



    public Users login(String username, String password){

        Users user= ud.getUserByUsername(username);

        if(user==null){
            logger.warning("Login failed: unknown user '" + username + "'");
            throw new UserNotFoundException("Username or password is incorrect");
        }


        if (!user.getPassword().equals(password)) {
            logger.warning("Login failed: wrong password for user '" + username + "'");
            throw new AuthenticationException("Username or password is incorrect");
        }

        if(!"manager".equalsIgnoreCase(user.getRole())){
            logger.warning("Login denied: user '" + username + "' is not a manager");
            throw new AuthenticationException("Username or password is incorrect");
        }

        logger.info("Login success: user '" + username + "'");
        return user;


    }


}
