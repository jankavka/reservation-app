package cz.reservation.service.exception;

public class TrainingSlotsInCollisionException extends RuntimeException {

    public TrainingSlotsInCollisionException(String errorMessage){
        super(errorMessage);
    }
}
