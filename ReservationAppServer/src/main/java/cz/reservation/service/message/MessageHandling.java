package cz.reservation.service.message;

import cz.reservation.constant.EventStatus;

public class MessageHandling {

    private MessageHandling() {

    }

    public static String entityNotFoundExceptionMessage(String serviceName, Long id) {
        var output = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
        return output + " with id " + id + " not found";
    }

    public static String successMessage(String serviceName, Long id, EventStatus status) {
        var output = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
        return output + " with id " + id + " " + status.getCode() + ".";
    }

    public static String notNullMessage(String serviceName) {
        var output = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
        return output + " must not be null";
    }

    public static String emptyListMessage(String serviceName) {
        var output = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
        if (output.endsWith("s")) {
            output = output + "es";
        } else {
            output = output + "s";
        }
        return "List of " + output + " is empty";
    }
}
