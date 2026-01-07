package cz.reservation.service.listener;

import com.notificationapi.NotificationApi;
import com.notificationapi.model.EmailOptions;
import com.notificationapi.model.NotificationRequest;
import com.notificationapi.model.User;
import cz.reservation.dto.CreatedUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UserCreatedListener {

    private final String clientId;

    private final String clientSecret;

    UserCreatedListener(
            @Value("${notification-api.client-id}") String clientId,
            @Value("${notification-api.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @EventListener
    public void handleCreatedUser(CreatedUserDto createdUserDto) throws IOException {
        var userId = createdUserDto.createdEntity.getId();
        var userEmail = createdUserDto.createdEntity.getEmail();
        var userName = createdUserDto.createdEntity.getFullName();
        var htmlContent = "<h3>Ahoj " + userName + "!</h3>" +
                "<p>Tvoje regestrace v rezervačním systému je tímto potvrzena!</p>";

        try (NotificationApi api = new NotificationApi(clientId, clientSecret)) {

            //Create instance of new user as a part of notification prepare
            User newUser = new User(userId.toString())
                    .setEmail(userEmail);

            NotificationRequest request = new NotificationRequest("reservation_app", newUser)
                    .setEmail(new EmailOptions()
                            .setSubject("Notification")
                            .setHtml(htmlContent));

            log.info("Sending notification request...");
            String response = api.send(request);
            log.info("Response: {}", response);


        } catch (IOException e) {
            throw new IOException("IOException during sending notifications");

        }

    }
}
