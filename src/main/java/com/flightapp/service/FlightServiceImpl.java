package com.flightapp.service;

import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Flight;
import com.flightapp.repository.FlightRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Mono<Flight> addInventory(InventoryRequest request) {
       
        return flightRepository
                .existsByAirlineNameAndFromPlaceAndToPlaceAndDepartureDateAndDepartureTime(
                        request.getAirlineName(),
                        request.getFromPlace(),
                        request.getToPlace(),
                        request.getDepartureDate(),
                        request.getDepartureTime()
                )
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Flight with same details already exists"
                        ));
                    }

                    Flight flight = Flight.builder()
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

                    return flightRepository.save(flight);
                });
    }

    @Override
    public Flux<Flight> searchFlights(SearchRequest request) {
       
        return flightRepository
                .findByFromPlaceAndToPlaceAndDepartureDate(
                        request.getFromPlace(),
                        request.getToPlace(),
                        request.getJourneyDate()
                )
                .filter(flight -> {
                    if (request.isRoundTrip()) {
                        return flight.getReturnDate() != null &&
                               flight.getReturnTime() != null &&
                               flight.getRoundTripPrice() != null;
                    }
                    return true; 
                });
    }

    @Override
    public Mono<Flight> getFlightById(String flightId) {
        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found")));
    }
}
