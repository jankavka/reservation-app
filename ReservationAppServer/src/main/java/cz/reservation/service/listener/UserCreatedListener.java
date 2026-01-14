package cz.reservation.service.listener;

import com.notificationapi.model.User;
import cz.reservation.dto.CreatedUserDto;
import cz.reservation.service.email.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UserCreatedListener {

    private final NotificationSender notificationSender;


    UserCreatedListener(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @EventListener
    public void handleCreatedUser(CreatedUserDto createdUserDto) throws IOException {
        var userId = createdUserDto.createdEntity.getId();
        var userEmail = createdUserDto.createdEntity.getEmail();
        var userName = createdUserDto.createdEntity.getFullName();
        var htmlContent = "<h3>Ahoj " + userName + "!</h3>" +
                "<p>Tvoje regestrace v rezervačním systému je tímto potvrzena!</p>";

        User newUser = new User(userId.toString())
                .setEmail(userEmail);

        notificationSender.sendEmail(htmlContent, newUser);


    }
}
