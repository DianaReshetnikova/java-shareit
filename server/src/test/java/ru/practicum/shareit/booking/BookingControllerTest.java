package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private static final String PATH = "/bookings";

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;



}
