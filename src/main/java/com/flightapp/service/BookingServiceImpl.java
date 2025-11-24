package com.flightapp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.flightapp.dto.BookingRequest;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.enums.BookingStatus;
import com.flightapp.model.enums.TripType;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Mono<Booking> bookTicket(String flightId, BookingRequest request) {
        
        if (request.getSeatNumbers() != null &&
                request.getNumberOfSeats() != null &&
                request.getSeatNumbers().size() != request.getNumberOfSeats()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Number of seats and seat numbers count must match"
                    
            ));
        }

        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found")))
                .flatMap(flight -> {
  
                    if (request.getTripType() == TripType.ROUND_TRIP) {
                        if (flight.getReturnDate() == null ||
                                flight.getReturnTime() == null ||
                                flight.getRoundTripPrice() == null) {
                            return Mono.error(new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Round trip not available for this flight"));
                        }
                    }

                    
                    return bookingRepository.findByFlightIdAndStatus(flightId, BookingStatus.CONFIRMED)
                            .flatMapIterable(Booking::getSeatNumbers)
                            .collect(Collectors.toSet())
                            .flatMap(existingSeats -> processBooking(flight, request, existingSeats));

                });
    }

    private Mono<Booking> processBooking(Flight flight,
                                         BookingRequest request,
                                         Set<String> alreadyBookedSeats) {

        
        boolean seatConflict = request.getSeatNumbers().stream()
                .anyMatch(alreadyBookedSeats::contains);

        if (seatConflict) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Some selected seats are already booked. Please choose different seats."
            ));
        }

        
        int currentBooked = flight.getBookedSeats() == null ? 0 : flight.getBookedSeats();
        if (currentBooked + request.getNumberOfSeats() > flight.getTotalSeats()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Not enough seats available"));
        }

       
        BigDecimal pricePerSeat;
        if (request.getTripType() == TripType.ROUND_TRIP) {
            pricePerSeat = flight.getRoundTripPrice() != null
                    ? flight.getRoundTripPrice()
                    : flight.getOneWayPrice();
        } else {
            pricePerSeat = flight.getOneWayPrice();
        }

        BigDecimal totalPrice = pricePerSeat.multiply(
                BigDecimal.valueOf(request.getNumberOfSeats()));

        String pnr = generatePnr(); 

        Booking booking = Booking.builder()
                .pnr(pnr)
                .flightId(flight.getId())
                .airlineName(flight.getAirlineName().name())
                .fromPlace(flight.getFromPlace().name())
                .toPlace(flight.getToPlace().name())
                .journeyDate(flight.getDepartureDate())
                .tripType(request.getTripType())
                .name(request.getName())
                .email(request.getEmail())
                .numberOfSeats(request.getNumberOfSeats())
                .passengers(request.getPassengers())
                .mealType(request.getMealType())
                .seatNumbers(request.getSeatNumbers())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .bookedAt(LocalDateTime.now())
                .build();

        int newBooked = currentBooked + request.getNumberOfSeats();
        flight.setBookedSeats(newBooked);

        return flightRepository.save(flight)
                .then(bookingRepository.save(booking));
    }

    @Override
    public Mono<Booking> getTicketByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found")));
    }

    @Override
    public Flux<Booking> getBookingHistory(String email) {
        return bookingRepository.findByEmailOrderByBookedAtDesc(email);
    }

    @Override
    public Mono<Void> cancelTicket(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found")))
                .flatMap(booking ->
                        flightRepository.findById(booking.getFlightId())
                                .switchIfEmpty(Mono.error(new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Flight not found")))
                                .flatMap(flight -> {

                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDateTime journeyDateTime = flight.getDepartureDate()
                                            .atTime(flight.getDepartureTime());

                                    long hours = ChronoUnit.HOURS.between(now, journeyDateTime);
                                    if (hours < 24) {
                                        return Mono.error(new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Cancellation allowed only 24 hours before journey"));
                                    }

                                    booking.setStatus(BookingStatus.CANCELLED);
                                    booking.setCancelledAt(LocalDateTime.now());

                                    int currentBooked = flight.getBookedSeats() == null ? 0 : flight.getBookedSeats();
                                    int updatedBooked = Math.max(0, currentBooked - booking.getNumberOfSeats());
                                    flight.setBookedSeats(updatedBooked);

                                    return flightRepository.save(flight)
                                            .then(bookingRepository.save(booking))
                                            .then();
                                })
                );
    }

    private String generatePnr() {
        return "PNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
