package uk.ac.ed.inf.pizzadronz.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PizzaDronzException {
    private final String message;
    private final Throwable throwable;
    private final HttpStatus httpStatus;

    public PizzaDronzException(String message, Throwable throwable, HttpStatus httpStatus) {
        this.message = message;
        this.throwable = throwable;
        this.httpStatus = httpStatus;
    }

    public ResponseEntity<String> handleJSonMappingException(JsonMappingException ex) {
        return new ResponseEntity<>("String" + ex.getOriginalMessage(), HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getMessage() {
        return message;
    }
}
