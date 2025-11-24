package com.flightapp.controller;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.enums.*;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

@WebFluxTest(controllers = FlightController.class)
class FlightControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private FlightService flightService;

    @MockBean
    private BookingService bookingService;

    private Flight flight;
    private Booking booking;

    @BeforeEach
    void setup() {

        flight = Flight.builder()
                .id("F1")
                .airlineName(AirlineName.INDIGO)
                .fromPlace(PlaceName.HYDERABAD)
                .toPlace(PlaceName.DELHI)
                .departureDate(LocalDate.now())
                .departureTime(LocalTime.NOON)
                .oneWayPrice(BigDecimal.valueOf(5000))
                .totalSeats(100)
                .bookedSeats(0)
                .build();

        booking = Booking.builder()
                .id("B1")
                .pnr("PNR1234")
                .flightId("F1")
                .airlineName("INDIGO")
                .fromPlace("HYDERABAD")
                .toPlace("DELHI")
                .journeyDate(LocalDate.now())
                .tripType(TripType.ONE_WAY)
                .name("Sravya")
                .email("test@example.com")
                .numberOfSeats(1)
                .totalPrice(BigDecimal.valueOf(5000))
                .mealType(MealType.VEG)
                .passengers(Collections.emptyList())
                .seatNumbers(Collections.emptyList())
                .status(BookingStatus.CONFIRMED)
                .build();
    }

    @Test
    void testAddInventory() {
        InventoryRequest req = new InventoryRequest();
        req.setAirlineName(AirlineName.INDIGO);
        req.setFromPlace(PlaceName.HYDERABAD);
        req.setToPlace(PlaceName.DELHI);
        req.setDepartureDate(LocalDate.now());
        req.setDepartureTime(LocalTime.NOON);
        req.setTotalSeats(50);
        req.setOneWayPrice(BigDecimal.valueOf(5000));

        Mockito.when(flightService.addInventory(Mockito.any())).thenReturn(Mono.just(flight));

        webClient.post()
                .uri("/api/v1.0/flight/airline/inventory")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void testSearchFlights() {
        SearchRequest request = new SearchRequest();
        request.setFromPlace(PlaceName.HYDERABAD);
        request.setToPlace(PlaceName.DELHI);
        request.setJourneyDate(LocalDate.now());

        Mockito.when(flightService.searchFlights(Mockito.any())).thenReturn(Flux.just(flight));

        webClient.post()
                .uri("/api/v1.0/flight/search")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Flight.class);
    }

    @Test
    void testGetTicket() {
        Mockito.when(bookingService.getTicketByPnr("PNR1234"))
                .thenReturn(Mono.just(booking));

        webClient.get()
                .uri("/api/v1.0/flight/ticket/PNR1234")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Booking.class);
    }
}
