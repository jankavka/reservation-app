package cz.reservation.service.exception;

public class PlayerNotAdultException extends Exception {

    public PlayerNotAdultException(String errorMessage){
        super(errorMessage);
    }
}
