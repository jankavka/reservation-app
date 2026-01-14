package cz.reservation.service.email;

import com.notificationapi.model.User;

public interface NotificationSender {

    void sendEmail(String message, User recipient);

    void sendSms(String message, User recipient);
}
