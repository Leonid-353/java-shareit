package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.TestDataUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    private static ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setupObjectMapper() {
        objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        javaTimeModule.addDeserializer(
                LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        objectMapper.registerModule(javaTimeModule);
    }

    @Test
    void createBooking() throws Exception {
        NewBookingRequest newBookingRequest = TestDataUtils.createNewBookingRequestAvailableItem();
        Booking booking = TestDataUtils.createBookingAvailableItemFromNewBookingRequest();
        User booker = booking.getBooker();
        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        when(bookingService.createBooking(newBookingRequest, booker.getId()))
                .thenReturn(dto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(newBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(Constants.X_SHARER_USER_ID, booker.getId()))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(bookingService, times(1))
                .createBooking(newBookingRequest, booker.getId());
    }

    @Test
    void findBooking() throws Exception {
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User user = TestDataUtils.createUser();
        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        when(bookingService.findBooking(booking.getId(), user.getId()))
                .thenReturn(dto);

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(Constants.X_SHARER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(bookingService, times(1))
                .findBooking(booking.getId(), user.getId());
    }


    @Test
    void findBookings() throws Exception {
        String state = "PAST";
        Booking booking = TestDataUtils.createBookingAvailableItemStatePast();
        User booker = booking.getBooker();
        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        when(bookingService.findBookingsByUserId(BookingState.PAST, booker.getId(), false))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header(Constants.X_SHARER_USER_ID, booker.getId())
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));

        verify(bookingService, times(1))
                .findBookingsByUserId(BookingState.PAST, booker.getId(), false);
    }

    @Test
    void findBookingsForOwner() throws Exception {
        String state = "CURRENT";
        Booking booking = TestDataUtils.createBookingAvailableItemStateCurrent();
        User owner = booking.getItem().getOwner();
        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        when(bookingService.findBookingsByUserId(BookingState.CURRENT, owner.getId(), true))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header(Constants.X_SHARER_USER_ID, owner.getId())
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));

        verify(bookingService, times(1))
                .findBookingsByUserId(BookingState.CURRENT, owner.getId(), true);
    }

    @Test
    void approvedBooking() throws Exception {
        String approved = "true";
        Booking booking = TestDataUtils.createBookingAvailableItem();
        User owner = booking.getItem().getOwner();
        BookingDto dto = BookingMapper.mapToBookingDto(booking);
        dto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approvedBooking(booking.getId(), owner.getId(), true))
                .thenReturn(dto);

        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header(Constants.X_SHARER_USER_ID, owner.getId())
                        .param("approved", approved))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(bookingService, times(1))
                .approvedBooking(booking.getId(), owner.getId(), true);
    }
}