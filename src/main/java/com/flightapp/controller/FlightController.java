package com.flightapp.controller;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.InventoryRequest;
import com.flightapp.dto.SearchRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

//    adding flights
    
    @PostMapping(
            value = "/airline/inventory",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> addInventory(@Valid @RequestBody InventoryRequest request) {
        return flightService.addInventory(request)
                .map(Flight::getId);
    }

    // Searching for flight
    @PostMapping("/search")
    public Flux<Flight> searchFlights(@Valid @RequestBody SearchRequest request) {
        return flightService.searchFlights(request);
    }

    // booking
    @PostMapping("/booking/{flightId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Booking> bookTicket(@PathVariable("flightId") String flightId,
                                    @Valid @RequestBody BookingRequest request) {
        return bookingService.bookTicket(flightId, request);
    }

    // get by pnr
    @GetMapping("/ticket/{pnr}")
    public Mono<Booking> getTicket(@PathVariable("pnr") String pnr) {
        return bookingService.getTicketByPnr(pnr);
    }

    // get by email
    @GetMapping("/booking/history/{email}")
    public Flux<Booking> getBookingHistory(@PathVariable("email") String email) {
        return bookingService.getBookingHistory(email);
    }

    // cancel by pnr
    @DeleteMapping("/booking/cancel/{pnr}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> cancelTicket(@PathVariable("pnr") String pnr) {
        return bookingService.cancelTicket(pnr);
    }
}
