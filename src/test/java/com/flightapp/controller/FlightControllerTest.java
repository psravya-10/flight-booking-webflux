package com.flightapp.controller;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.PassengerInfo;
import com.flightapp.model.enums.*;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebFluxTest(controllers = FlightController.class)
public class FlightControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FlightService flightService;

    @MockBean
    private BookingService bookingService;

    private Flight flight;
    private Booking booking;

    @BeforeEach
    void init() {
        flight = Flight.builder()
                .id("F1")
                .airlineName(AirlineName.INDIGO)
                .fromPlace(PlaceName.HYDERABAD)
                .toPlace(PlaceName.DELHI)
                .departureDate(LocalDate.now())
                .departureTime(LocalTime.now())
                .oneWayPrice(BigDecimal.valueOf(2000))
                .totalSeats(100)
                .bookedSeats(10)
                .build();

        booking = Booking.builder()
                .id("B1")
                .pnr("PNR-12345678")
                .flightId("F1")
                .airlineName("INDIGO")
                .fromPlace("HYDERABAD")
                .toPlace("DELHI")
                .email("sravya@gmail.com")
                .tripType(TripType.ONE_WAY)
                .numberOfSeats(1)
                .passengers(List.of(new PassengerInfo("Sravya", "F", 21)))
                .mealType(MealType.VEG)
                .seatNumbers(List.of("1A"))
                .status(BookingStatus.CONFIRMED)
                .build();
    }

    @Test
    void testAddInventory() {
        InventoryRequest request = new InventoryRequest();
        request.setAirlineName(AirlineName.INDIGO);
        request.setFromPlace(PlaceName.HYDERABAD);
        request.setToPlace(PlaceName.DELHI);
        request.setDepartureDate(LocalDate.now());
        request.setDepartureTime(LocalTime.now());
        request.setTotalSeats(100);
        request.setOneWayPrice(BigDecimal.valueOf(2000));

        Mockito.when(flightService.addInventory(Mockito.any()))
                .thenReturn(Mono.just(flight));

        webTestClient.post()
                .uri("/api/v1.0/flight/airline/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo("F1");
    }

    @Test
    void testSearchFlights() {
        SearchRequest request = new SearchRequest();
        request.setFromPlace(PlaceName.HYDERABAD);
        request.setToPlace(PlaceName.DELHI);
        request.setJourneyDate(LocalDate.now());

        Mockito.when(flightService.searchFlights(Mockito.any()))
                .thenReturn(Flux.just(flight));

        webTestClient.post()
                .uri("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Flight.class)
                .hasSize(1);
    }

    @Test
    void testBookTicket() {
        BookingRequest request = new BookingRequest();
        request.setName("Sravya");
        request.setEmail("sravya@gmail.com");
        request.setNumberOfSeats(1);
        request.setPassengers(List.of(new PassengerInfo("A", "F", 21)));
        request.setMealType(MealType.VEG);
        request.setSeatNumbers(List.of("1A"));
        request.setTripType(TripType.ONE_WAY);

        Mockito.when(bookingService.bookTicket(Mockito.eq("F1"), Mockito.any()))
                .thenReturn(Mono.just(booking));

        webTestClient.post()
                .uri("/api/v1.0/flight/booking/F1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Booking.class);
    }

    @Test
    void testGetTicket() {
        Mockito.when(bookingService.getTicketByPnr("PNR-12345678"))
                .thenReturn(Mono.just(booking));

        webTestClient.get()
                .uri("/api/v1.0/flight/ticket/PNR-12345678")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Booking.class);
    }

    @Test
    void testBookingHistory() {
        Mockito.when(bookingService.getBookingHistory("sravya@gmail.com"))
                .thenReturn(Flux.just(booking));

        webTestClient.get()
                .uri("/api/v1.0/flight/booking/history/sravya@gmail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Booking.class)
                .hasSize(1);
    }

    @Test
    void testCancelTicket() {
        Mockito.when(bookingService.cancelTicket("PNR-12345678"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1.0/flight/booking/cancel/PNR-12345678")
                .exchange()
                .expectStatus().isNoContent();
    }
}
