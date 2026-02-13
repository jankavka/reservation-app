package cz.reservation.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.reservation.constant.BookingStatus;
import cz.reservation.controller.AttendanceController;
import cz.reservation.controller.advice.GlobalExceptionHandler;
import cz.reservation.dto.AttendanceDto;
import cz.reservation.dto.BookingDto;
import cz.reservation.entity.filter.AttendanceFilter;
import cz.reservation.service.serviceinterface.AttendanceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Objects;

import static cz.reservation.service.message.MessageHandling.entityNotFoundExceptionMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AttendanceControllerTest {

    @MockitoBean
    AttendanceService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldCreateNewAttendance() throws Exception {
        var bookingDto = new BookingDto(1L, null, null, BookingStatus.CONFIRMED, null);
        var attendanceDto = new AttendanceDto(null, bookingDto, Boolean.TRUE, "N", null);
        var createdAttendanceDto = new AttendanceDto(1L, bookingDto, Boolean.TRUE, "N", LocalDateTime.now());
        when(service.createAttendance(attendanceDto)).thenReturn(createdAttendanceDto);

        mockMvc.perform(post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.present").value(true));

        verify(service).createAttendance(attendanceDto);


    }

    @Test
    void shouldThrowExceptionWhenDtoNoValid() throws Exception {
        var attendanceDto = new AttendanceDto(null, null, null, null, null);

        mockMvc.perform(post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.booking").value("Booking must not be null"));

        verify(service, never()).createAttendance(attendanceDto);
    }

    @Test
    void shouldGetAttendance() throws Exception {
        var id = 1L;
        var attendanceDto = new AttendanceDto(1L, null, null, null, null);

        when(service.getAttendance(id)).thenReturn(attendanceDto);

        mockMvc.perform(get("/api/attendance/1"))
                .andExpect(jsonPath("$.id").value(1));

        verify(service).getAttendance(id);

    }

    @Test
    void shouldReturnNotFound() throws Exception {

        when(service.getAttendance(99L))
                .thenThrow(new EntityNotFoundException(entityNotFoundExceptionMessage(
                        "attendance", 99L)));

        mockMvc.perform(get("/api/attendance/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Attendance with id 99 not found"));


        verify(service, times(1)).getAttendance(99L);


    }

    @Test
    void shouldReturnListOfAttendances() throws Exception {
        var listOfAttendances = new ArrayList<AttendanceDto>();
        listOfAttendances.add(new AttendanceDto(1L, null, null, null, null));
        when(service.getAllAttendances(any(AttendanceFilter.class))).thenReturn(listOfAttendances);

        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).getAllAttendances(any(AttendanceFilter.class));
    }

    @Test
    void shouldReturnEmptyListAndNotThrow() throws Exception {
        var emptyList = new ArrayList<AttendanceDto>();

        when(service.getAllAttendances(any(AttendanceFilter.class))).thenReturn(emptyList);

        mockMvc.perform(get("/api/attendance"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).getAllAttendances(any(AttendanceFilter.class));

    }

    @Test
    void shouldEditAttendance() throws Exception {
        var id = 1L;
        var date = LocalDateTime.of(2026, Month.AUGUST, 10, 10, 0);
        var bookingDto = new BookingDto(
                1L, null, null, BookingStatus.CONFIRMED, date.minusHours(1));
        var attendanceDto = new AttendanceDto(1L, bookingDto, Boolean.TRUE, "N", date);


        doNothing().when(service).editAttendance(attendanceDto, id);

        mockMvc.perform(put("/api/attendance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andExpect(status().isNoContent());

        verify(service).editAttendance(attendanceDto, id);

    }

    @Test
    void shouldThrowExceptionWhenNoBooking() throws Exception {
        var date = LocalDateTime.of(2026, Month.AUGUST, 10, 10, 0);
        var attendanceDto = new AttendanceDto(1L, null, Boolean.TRUE, "N", date);


        mockMvc.perform(put("/api/attendance/1")
                        .content(objectMapper.writeValueAsString(attendanceDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.booking").value("Booking must not be null"));

        verifyNoInteractions(service);


    }

    @Test
    void shouldThrowExceptionWhenIdNotNumber_whileEditing() throws Exception {
        var date = LocalDateTime.of(2026, Month.AUGUST, 10, 10, 0);
        var bookingDto = new BookingDto(1L, null, null, BookingStatus.CONFIRMED, null);
        var attendanceDto = new AttendanceDto(1L, bookingDto, Boolean.TRUE, "N", date);

        mockMvc.perform(put("/api/attendance/a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceDto)))
                .andDo(print())
                .andExpect(result -> assertEquals(
                        Objects.requireNonNull(result.getResolvedException()).getClass(),
                        MethodArgumentTypeMismatchException.class));

        verifyNoInteractions(service);
    }

    @Test
    void shouldDeleteAttendance() throws Exception {
        var id = 1L;

        doNothing().when(service).deleteAttendance(id);

        mockMvc.perform(delete("/api/attendance/1"))
                .andExpect(status().isNoContent());

        verify(service).deleteAttendance(id);

    }

    @Test
    void shouldThrowExceptionWhenIdNotNumber_whileDeleting() throws Exception {

        mockMvc.perform(delete("/api/attendance/a"))
                .andDo(print())
                .andExpect(result -> assertEquals(
                        Objects.requireNonNull(result.getResolvedException()).getClass(),
                        MethodArgumentTypeMismatchException.class));

        verify(service, times(0)).deleteAttendance(1L);

    }


}
