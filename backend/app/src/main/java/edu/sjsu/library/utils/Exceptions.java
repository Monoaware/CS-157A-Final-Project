package edu.sjsu.library.exceptions;

public class BookAlreadyReturnedException extends RuntimeException {
    public BookAlreadyReturnedException(String message) {
        super(message);
    }
    public BookAlreadyReturnedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class RenewalNotAllowedException extends RuntimeException {
    public RenewalNotAllowedException(String message) {
        super(message);
    }
    public RenewalNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class FinePaymentNotAllowedException extends RuntimeException {
    public FinePaymentNotAllowedException(String message) {
        super(message);
    }
    public RenewalNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class FineWaivementNotAllowedException extends RuntimeException {
    public FineWaivementNotAllowedException(String message) {
        super(message);
    }
    public FineWaivementNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class HoldChangeNotAllowedException extends RuntimeException {
    public HoldChangeNotAllowedException(String message) {
        super(message);
    }
    public HoldChangeNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class UserStatusChangeNotAllowedException extends RuntimeException {
    public UserStatusChangeNotAllowedException(String message) {
        super(message);
    }
    public UserStatusChangeNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}