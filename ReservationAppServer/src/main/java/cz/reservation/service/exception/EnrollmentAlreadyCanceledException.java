package cz.reservation.service.exception;

public class EnrollmentAlreadyCanceledException extends RuntimeException{

    public EnrollmentAlreadyCanceledException(String errorMessage){
        super(errorMessage);
    }
}
