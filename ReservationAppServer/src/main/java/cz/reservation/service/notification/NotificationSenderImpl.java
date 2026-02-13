package cz.reservation.service.notification;

import com.notificationapi.NotificationApi;
import com.notificationapi.model.EmailOptions;
import com.notificationapi.model.NotificationRequest;
import com.notificationapi.model.SmsOptions;
import com.notificationapi.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class NotificationSenderImpl implements NotificationSender {

    private final String clientId;

    private final String clientSecret;

    private static final String EXCEPTION_MESSAGE = "IO exception during notification process: ";

    public NotificationSenderImpl(
            @Value("${notification-api.client-id}") String clientId,
            @Value("${notification-api.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void sendEmail(String message, User recipient) {
        try (NotificationApi api = new NotificationApi(clientId, clientSecret)) {
            NotificationRequest request = new NotificationRequest("reservation_app", recipient)
                    .setEmail(new EmailOptions()
                            .setSubject("Notification")
                            .setHtml(message));

            sendNotificationRequest(api, request);

        } catch (IOException e) {
            log.error("{}{}", EXCEPTION_MESSAGE, e.getMessage());

        }
    }

    @Override
    public void sendSms(String message, User recipient) {
        try (NotificationApi api = new NotificationApi(clientId, clientSecret)) {
            NotificationRequest request = new NotificationRequest("reservation_app", recipient)
                    .setSms(new SmsOptions()
                            .setMessage(message));

            sendNotificationRequest(api, request);
        } catch (IOException e) {
            log.error("{}{}", EXCEPTION_MESSAGE, e.getMessage());
        }
    }

    private void sendNotificationRequest(NotificationApi api, NotificationRequest request) {
        log.info("Sending notification request...");
        String response = api.send(request);
        log.info("Response: {}", response);
    }

}
