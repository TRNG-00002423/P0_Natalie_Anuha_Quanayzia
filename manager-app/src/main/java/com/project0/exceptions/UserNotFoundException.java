package com.project0.exceptions;

public class UserNotFoundException extends  RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
