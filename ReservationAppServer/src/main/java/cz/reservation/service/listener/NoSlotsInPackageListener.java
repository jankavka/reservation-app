package cz.reservation.service.listener;

import com.notificationapi.model.User;
import cz.reservation.dto.NoSlotsInPackageDto;
import cz.reservation.dto.UserDto;
import cz.reservation.service.email.NotificationSender;
import cz.reservation.service.serviceinterface.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class NoSlotsInPackageListener {

    private final NotificationSender notificationSender;

    private final UserService userService;

    public NoSlotsInPackageListener(NotificationSender notificationSender, UserService userService) {
        this.notificationSender = notificationSender;
        this.userService = userService;
    }


    @EventListener
    public void handleEvent(NoSlotsInPackageDto noSlotsInPackage) {
        var relatedPlayer = noSlotsInPackage.getPackageEntity().getPlayer();
        var playerName = relatedPlayer.getFullName();
        var relatedUser = relatedPlayer.getParent();
        var userName = relatedUser.getFullName();
        var numberOfSlots = noSlotsInPackage.getPackageEntity().getAvailableSlots();

        //Sets and sends messages for customer
        User customer = new User(relatedUser.getId().toString())
                .setEmail(relatedUser.getEmail())
                .setNumber(relatedUser.getTelephoneNumber());

        var emailCustomerMessage = "<h3>Dobrý den, " + userName + "</h3>" +
                "<p><strong>Upozornění</strong></p>" +
                "<p>Balíček na účtu hráče " + playerName + " je " + numberOfSlots + " dostupných slotů.</p>" +
                "<p>Pokud chcete služby využívat i nadále, dokupte sloty</p>";
        var smsCustomerMessage = "Dobrý den, balíček na účtu hráče " + playerName +
                " je " + numberOfSlots + " dostupných slotů." +
                " Pokud Chcete služby využívat i nadále, dokupte sloty";


        notificationSender.sendEmail(emailCustomerMessage, customer);
        notificationSender.sendSms(smsCustomerMessage, customer);


        //Sets and sends messages for all admins
        var messageAdmin = "Notification: Hráč " + playerName + " uživatele "
                + userName + " má počet slotů v balíčku " + numberOfSlots;

        if (!userService.getAllAdmins().isEmpty()) {
            for (UserDto a : userService.getAllAdmins()) {
                User admin = new User(a.id().toString())
                        .setEmail(a.email())
                        .setNumber(a.telephoneNumber());
                notificationSender.sendEmail(messageAdmin, admin);
                notificationSender.sendSms(messageAdmin, admin);

            }
        }
    }
}
