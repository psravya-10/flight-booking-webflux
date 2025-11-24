package com.flightapp.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.model.Flight;
import com.flightapp.model.enums.AirlineName;
import com.flightapp.model.enums.PlaceName;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {
//checking the flights
    Flux<Flight> findByFromPlaceAndToPlaceAndDepartureDate(
            PlaceName fromPlace,
            PlaceName toPlace,
            LocalDate departureDate
    );

//    checking duplication
    Mono<Boolean> existsByAirlineNameAndFromPlaceAndToPlaceAndDepartureDateAndDepartureTime(
            AirlineName airlineName,
            PlaceName fromPlace,
            PlaceName toPlace,
            LocalDate departureDate,
            LocalTime departureTime
    );
}
