package cz.reservation.service.exception;

public class EmptyListException extends RuntimeException{

    public EmptyListException(String errorMessage){
        super(errorMessage);
    }
}
