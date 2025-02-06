package uk.ac.ed.inf.pizzadronz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//Handles exceptions globally
@ControllerAdvice
public class PizzaDronzExceptionHandler {

    //assigns the kind of exception that will be handled with this handler
    @ExceptionHandler(value = {InvalidBodyException.class})

    public ResponseEntity<Object> handleInvalidDistanceToException
            (InvalidBodyException ex) {
        PizzaDronzException pizzaDronzException = new PizzaDronzException(
                ex.getMessage(),
                ex.getCause(),
                HttpStatus.BAD_REQUEST
        );

        return new ResponseEntity<>(pizzaDronzException, HttpStatus.BAD_REQUEST);
    }
}
