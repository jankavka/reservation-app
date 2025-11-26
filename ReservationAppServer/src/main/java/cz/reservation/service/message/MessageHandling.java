package cz.reservation.service.message;

import cz.reservation.constant.EventStatus;

public class MessageHandling {

    private MessageHandling(){

    }

    public static String entityNotFoundExceptionMessage(String serviceName, Long id){
        var output = serviceName.substring(0,1).toUpperCase();
        return output + " with id " + id + " not found";
    }

    public static String successMessage(String serviceName, Long id, EventStatus status){
        var output = serviceName.substring(0,1).toUpperCase();
        return output + " with id " + id + " " + status.getCode() + ".";
    }

    public static String notNullMessage(String serviceName){
        var output = serviceName.substring(0,1).toUpperCase();
        return output + " must not be null";
    }
}
