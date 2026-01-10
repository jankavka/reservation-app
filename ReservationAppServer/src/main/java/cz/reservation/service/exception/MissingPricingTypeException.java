package cz.reservation.service.exception;

public class MissingPricingTypeException extends RuntimeException{

    public MissingPricingTypeException(String message){
        super(message);
    }
}
