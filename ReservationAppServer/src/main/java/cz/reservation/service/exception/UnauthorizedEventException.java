package cz.reservation.service.exception;

public class UnauthorizedEventException extends RuntimeException{

    public UnauthorizedEventException(String message){
        super(message);
    }
}
