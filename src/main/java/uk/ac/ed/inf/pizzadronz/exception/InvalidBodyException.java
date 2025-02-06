package uk.ac.ed.inf.pizzadronz.exception;

import org.springframework.http.HttpStatus;


public class InvalidBodyException extends RuntimeException{
    public InvalidBodyException(String message) {
        super(message);
    }

    public InvalidBodyException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
    }
}
