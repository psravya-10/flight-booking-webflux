package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.PassengerInfo;
import com.flightapp.model.enums.AirlineName;
import com.flightapp.model.enums.BookingStatus;
import com.flightapp.model.enums.MealType;
import com.flightapp.model.enums.PlaceName;
import com.flightapp.model.enums.TripType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl service;

    private Flight flight;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        flight = Flight.builder()
                .id("f1")
                .airlineName(AirlineName.INDIGO)
                .fromPlace(PlaceName.HYDERABAD)
                .toPlace(PlaceName.DELHI)
                .departureDate(LocalDate.now().plusDays(2))
                .departureTime(LocalTime.of(10, 30))
                .oneWayPrice(new BigDecimal("2500"))
                .totalSeats(100)
                .bookedSeats(0)
                .build();
    }

    @Test
    void testBookTicket_Success() {

        BookingRequest request = new BookingRequest();
        request.setName("Sravya");
        request.setEmail("sravya@example.com");
        request.setNumberOfSeats(1);
        request.setPassengers(List.of(new PassengerInfo("Ram", "M", 25)));
        request.setMealType(MealType.VEG);
        request.setSeatNumbers(List.of("10A"));
        request.setTripType(TripType.ONE_WAY);
       
        when(flightRepository.findById("f1")).thenReturn(Mono.just(flight));

        when(bookingRepository.findByFlightIdAndStatus("f1", BookingStatus.CONFIRMED))
                .thenReturn(Flux.empty());

        when(flightRepository.save(any())).thenReturn(Mono.just(flight));
        when(bookingRepository.save(any())).thenReturn(Mono.just(new Booking()));

        StepVerifier.create(service.bookTicket("f1", request))
                .expectNextCount(1)
                .verifyComplete();
    }
}
