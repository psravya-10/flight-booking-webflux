package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.model.Booking;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    Mono<Booking> bookTicket(String flightId, BookingRequest request);

    Mono<Booking> getTicketByPnr(String pnr);

    Flux<Booking> getBookingHistory(String email);

    Mono<Void> cancelTicket(String pnr);
}
