package cz.reservation.service.exception;

public class TrainingAlreadyStartedException extends RuntimeException {

    public TrainingAlreadyStartedException(String errorMessage) {
        super(errorMessage);
    }
}
