package edu.sjsu.library.exceptions;

public class FineWaivementNotAllowedException extends RuntimeException {
    public FineWaivementNotAllowedException(String message) { super(message); }
    public FineWaivementNotAllowedException(String message, Throwable cause) { super(message, cause); }
}