package cz.reservation.service.exception;

public class LowAgeException extends Exception {

    public LowAgeException(String errorMessage){
        super(errorMessage);
    }
}
