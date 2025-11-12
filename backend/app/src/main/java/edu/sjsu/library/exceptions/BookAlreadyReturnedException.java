package edu.sjsu.library.exceptions;

public class BookAlreadyReturnedException extends RuntimeException {
    public BookAlreadyReturnedException(String message) { super(message); }
    public BookAlreadyReturnedException(String message, Throwable cause) { super(message, cause); }
}