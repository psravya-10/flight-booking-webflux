package com.flightapp.service;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Flight;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {

    Mono<Flight> addInventory(InventoryRequest request);

    Flux<Flight> searchFlights(SearchRequest request);

    Mono<Flight> getFlightById(String flightId);
}
