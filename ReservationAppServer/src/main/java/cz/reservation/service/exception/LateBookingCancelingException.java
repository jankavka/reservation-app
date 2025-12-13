package cz.reservation.service.exception;

public class LateBookingCancelingException extends RuntimeException{

    public LateBookingCancelingException(String errorMessage){
        super(errorMessage);
    }
}
