package cz.reservation.service.listener;

import com.notificationapi.NotificationApi;
import com.notificationapi.model.EmailOptions;
import com.notificationapi.model.NotificationRequest;
import com.notificationapi.model.SmsOptions;
import com.notificationapi.model.User;
import cz.reservation.dto.CreatedBookingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class BookingCreatedListener {

    @Value("${notification-api.client-id}")
    private String clientId;

    @Value("${notification-api.client-secret}")
    private String clientSecret;

    @EventListener
    public void handleCreatedBooking(CreatedBookingDto createdBookingDto) throws IOException {


        var currentBooking = createdBookingDto.getBookingEntity();
        var group = createdBookingDto.getBookingEntity().getTrainingSlot().getGroup();
        var coach = group.getCoach();
        var trainingSlot = createdBookingDto.getBookingEntity().getTrainingSlot();
        var formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(trainingSlot.getStartAt().toLocalDate());
        var startsAt = DateTimeFormatter.ofPattern("HH:mm").format(trainingSlot.getStartAt());
        var endsAt = DateTimeFormatter.ofPattern("HH:mm").format(trainingSlot.getEndAt());
        var bookingStatus = currentBooking.getBookingStatus();
        var playerName = createdBookingDto.getBookingEntity().getPlayer().getFullName();
        var coachEmail = coach.getUser().getEmail();
        var coachTelephone = coach.getUser().getTelephoneNumber();

        var notificationSmsString = "New booking in training slot: " + "\n"
                + "\n"
                + "Player: " + playerName + "\n"
                + "Group: " + group + "\n"
                + "Date: " + formattedDate + "\n"
                + "Starts at:  " + startsAt + "\n"
                + "Ends at: " + endsAt + "\n"
                + "Status " + bookingStatus;

        var notificationEmailString = "<h4>New Booking in trainingSlot:</h4>" +
                "<p>Player: " + playerName + "</p>" +
                "<p>Group: " + group + "</p>" +
                "<p>Date: " + formattedDate + "</p>" +
                "<p>Starts at: " + startsAt + "</p>" +
                "<p>Ends at: " + endsAt + "</p>" +
                "<p>Status: " + bookingStatus + "</p>";


        try (NotificationApi api = new NotificationApi(clientId, clientSecret)) {

            // Create user
            User user = new User(coach.getId().toString())
                    .setEmail(coachEmail)
                    .setNumber(coachTelephone);


            // Create and send notification request
            NotificationRequest request = new NotificationRequest("reservation_app", user)
                    .setEmail(new EmailOptions()
                            .setSubject("Notification")
                            .setHtml(notificationEmailString))
                    .setSms(new SmsOptions()
                            .setMessage(notificationSmsString));

            log.info("Sending notification request...");
            String response = api.send(request);
            log.info("Response: {}", response);

        } catch (IOException e) {
            throw new IOException("IOException during sending notifications");
        }


    }
}
