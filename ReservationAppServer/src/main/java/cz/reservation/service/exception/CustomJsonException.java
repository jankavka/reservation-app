package cz.reservation.service.exception;

public class CustomJsonException extends RuntimeException{

    public CustomJsonException(String message){
        super(message);
    }
}
