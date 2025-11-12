package edu.sjsu.library.exceptions;

public class UserStatusChangeNotAllowedException extends RuntimeException {
    public UserStatusChangeNotAllowedException(String message) { super(message); }
    public UserStatusChangeNotAllowedException(String message, Throwable cause) { super(message, cause); }
}