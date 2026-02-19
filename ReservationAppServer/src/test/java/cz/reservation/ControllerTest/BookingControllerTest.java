package cz.reservation.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.reservation.constant.BookingStatus;
import cz.reservation.controller.BookingController;
import cz.reservation.controller.advice.GlobalExceptionHandler;
import cz.reservation.dto.BookingDto;
import cz.reservation.dto.PlayerDto;
import cz.reservation.dto.TrainingSlotDto;
import cz.reservation.service.serviceinterface.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class BookingControllerTest {

    @MockitoBean
    BookingService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldCreateBookingWithNoException() throws Exception {
        var trainingSlotDto = new TrainingSlotDto(
                1L, null, null, null, null,
                null, null, null, null, null);
        var playerDto = new PlayerDto(
                1L, null, null, null, null,
                null, null, null);
        var date = LocalDateTime.now();
        var bookingDtoToSave = new BookingDto(null, trainingSlotDto, playerDto, null, null);
        var savedBookingDto = new BookingDto(1L, trainingSlotDto, playerDto, BookingStatus.CONFIRMED, date);

        when(service.createBooking(bookingDtoToSave)).thenReturn(savedBookingDto);

        mockMvc.perform(post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoToSave)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).createBooking(bookingDtoToSave);
    }


}
