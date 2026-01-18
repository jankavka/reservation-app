package cz.reservation.service.exception;

public class EnrollmentNoActiveException extends RuntimeException{

    public EnrollmentNoActiveException(String errorMessage){
        super(errorMessage);
    }
}
