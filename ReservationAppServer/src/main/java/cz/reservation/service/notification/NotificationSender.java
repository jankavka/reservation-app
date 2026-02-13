package cz.reservation.service.notification;

import com.notificationapi.model.User;

public interface NotificationSender {

    void sendEmail(String message, User recipient);

    void sendSms(String message, User recipient);
}
