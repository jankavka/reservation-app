package cz.reservation.service.exception;

public class UnsupportedTimeRangeException extends RuntimeException{

    public UnsupportedTimeRangeException(String errorMessage){
        super(errorMessage);
    }
}
