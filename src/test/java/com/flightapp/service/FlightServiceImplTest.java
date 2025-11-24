package com.flightapp.service;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.model.Flight;
import com.flightapp.model.enums.AirlineName;
import com.flightapp.model.enums.PlaceName;
import com.flightapp.repository.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private InventoryRequest request;
    private Flight savedFlight;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        request = new InventoryRequest();
        request.setAirlineName(AirlineName.INDIGO);
        request.setAirlineLogo("logo.png");
        request.setFromPlace(PlaceName.HYDERABAD);
        request.setToPlace(PlaceName.CHENNAI);
        request.setDepartureDate(LocalDate.now().plusDays(1));
        request.setDepartureTime(LocalTime.NOON);
        request.setReturnDate(LocalDate.now().plusDays(3));
        request.setReturnTime(LocalTime.NOON.plusHours(2));
        request.setOneWayPrice(new BigDecimal("3000"));
        request.setRoundTripPrice(new BigDecimal("5500"));
        request.setTotalSeats(100);

        savedFlight = Flight.builder()
                .id("1")
                .airlineName(request.getAirlineName())
                .airlineLogo(request.getAirlineLogo())
                .fromPlace(request.getFromPlace())
                .toPlace(request.getToPlace())
                .departureDate(request.getDepartureDate())
                .departureTime(request.getDepartureTime())
                .returnDate(request.getReturnDate())
                .returnTime(request.getReturnTime())
                .oneWayPrice(request.getOneWayPrice())
                .roundTripPrice(request.getRoundTripPrice())
                .totalSeats(request.getTotalSeats())
                .bookedSeats(0)
                .build();
    }

    @Test
    void testAddInventory_Success() {
        when(flightRepository.existsByAirlineNameAndFromPlaceAndToPlaceAndDepartureDateAndDepartureTime(
                any(), any(), any(), any(), any()
        )).thenReturn(Mono.just(false));

        when(flightRepository.save(any())).thenReturn(Mono.just(savedFlight));

        StepVerifier.create(flightService.addInventory(request))
                .expectNext(savedFlight)
                .verifyComplete();
    }

    @Test
    void testAddInventory_FlightAlreadyExists() {
        when(flightRepository.existsByAirlineNameAndFromPlaceAndToPlaceAndDepartureDateAndDepartureTime(
                any(), any(), any(), any(), any()
        )).thenReturn(Mono.just(true));

        StepVerifier.create(flightService.addInventory(request))
                .expectErrorMatches(ex -> ex.getMessage().contains("already exists"))
                .verify();
    }
}
