package edu.sjsu.library.exceptions;

public class AuthorizationFailedException extends RuntimeException {
    public AuthorizationFailedException(String message) {
        super(message);
    }
    
    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}