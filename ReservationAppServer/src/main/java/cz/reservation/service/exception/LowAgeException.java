package cz.reservation.service.exception;

public class LowAgeException extends RuntimeException {

    public LowAgeException(String errorMessage){
        super(errorMessage);
    }
}
