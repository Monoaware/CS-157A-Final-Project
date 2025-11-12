package edu.sjsu.library.exceptions;

public class HoldChangeNotAllowedException extends RuntimeException {
    public HoldChangeNotAllowedException(String message) { super(message); }
    public HoldChangeNotAllowedException(String message, Throwable cause) { super(message, cause); }
}