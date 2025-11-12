package edu.sjsu.library.exceptions;

public class RenewalNotAllowedException extends RuntimeException {
    public RenewalNotAllowedException(String message) { super(message); }
    public RenewalNotAllowedException(String message, Throwable cause) { super(message, cause); }
}