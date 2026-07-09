package cz.reservation.service.listener;

import com.notificationapi.model.User;
import cz.reservation.dto.CreatedUserByAdminDto;
import cz.reservation.service.notification.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserCreatedByAdminListener {

    private final NotificationSender notificationSender;

    //@EventListener
    public void handleCreateUserByAdmin(CreatedUserByAdminDto createUserByAdminDto) {
        var userId = createUserByAdminDto.userEntity.getId();
        var userEmail = createUserByAdminDto.userEntity.getEmail();
        var userName = createUserByAdminDto.userEntity.getFullName();
        var password = createUserByAdminDto.userEntity.getPassword();
        var htmlContent = "<h3>Ahoj " + userName + "!</h3>" +
                "<p>Tvoje registrace v rezervačním systému je tímto potvrzena!</p>" +
                "<p>Vaše heslo je: " + password + ". Změňte jej co nejdříve.</p>";

        User newUser = new User(userId.toString()).setEmail(userEmail);

        notificationSender.sendEmail(htmlContent, newUser);


    }


}
