package edu.sjsu.library.exceptions;

public class FinePaymentNotAllowedException extends RuntimeException {
    public FinePaymentNotAllowedException(String message) { super(message); }
    public FinePaymentNotAllowedException(String message, Throwable cause) { super(message, cause); }
}