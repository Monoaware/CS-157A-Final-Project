package edu.sjsu.library.exceptions;

public class CheckoutNotAllowedException extends RuntimeException {
    public CheckoutNotAllowedException(String message) { super(message); }
    public CheckoutNotAllowedException(String message, Throwable cause) { super(message, cause); }
}