package com.flightapp.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.flightapp.model.Booking;
import com.flightapp.model.enums.BookingStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {

    Mono<Booking> findByPnr(String pnr);

    Flux<Booking> findByEmailOrderByBookedAtDesc(String email);

    Flux<Booking> findByFlightIdAndStatus(String flightId, BookingStatus status);
}
