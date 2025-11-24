package com.flightapp.service;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Flight;
import com.flightapp.model.enums.AirlineName;
import com.flightapp.model.enums.PlaceName;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightServiceImpl;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private InventoryRequest inventoryRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        inventoryRequest = new InventoryRequest();
        inventoryRequest.setAirlineName(AirlineName.INDIGO);
        inventoryRequest.setFromPlace(PlaceName.HYDERABAD);
        inventoryRequest.setToPlace(PlaceName.DELHI);
        inventoryRequest.setDepartureDate(LocalDate.now());
        inventoryRequest.setDepartureTime(LocalTime.now());
        inventoryRequest.setTotalSeats(100);
        inventoryRequest.setOneWayPrice(BigDecimal.valueOf(2000));
    }

    @Test
    void testAddInventory_Success() {
        when(flightRepository.existsByAirlineNameAndFromPlaceAndToPlaceAndDepartureDateAndDepartureTime(
                any(), any(), any(), any(), any()
        )).thenReturn(Mono.just(false));

        when(flightRepository.save(any()))
                .thenReturn(Mono.just(new Flight()));

        StepVerifier.create(flightService.addInventory(inventoryRequest))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testSearchFlights() {
        SearchRequest request = new SearchRequest();
        request.setFromPlace(PlaceName.HYDERABAD);
        request.setToPlace(PlaceName.DELHI);
        request.setJourneyDate(LocalDate.now());

        when(flightRepository.findByFromPlaceAndToPlaceAndDepartureDate(any(), any(), any()))
                .thenReturn(Flux.just(new Flight()));

        StepVerifier.create(flightService.searchFlights(request))
                .expectNextCount(1)
                .verifyComplete();
    }
}
