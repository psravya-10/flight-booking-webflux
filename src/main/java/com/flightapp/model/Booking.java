package com.flightapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.flightapp.model.enums.BookingStatus;
import com.flightapp.model.enums.MealType;
import com.flightapp.model.enums.TripType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String pnr;

    @NotBlank(message = "Flight id cannot be blank")
    private String flightId;

    @NotBlank(message = "Airline name cannot be blank")
    private String airlineName;

    @NotBlank(message = "From place cannot be blank")
    private String fromPlace;

    @NotBlank(message = "To place cannot be blank")
    private String toPlace;

    @NotNull(message = "Journey date cannot be null")
    private LocalDate journeyDate;

    @NotNull(message = "Trip type cannot be null")
    private TripType tripType;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least one seat must be booked")
    private Integer numberOfSeats;

    @Valid
    @NotNull(message = "Passengers list cannot be null")
    @Size(min = 1, message = "At least one passenger is required")
    private List<PassengerInfo> passengers;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Seat numbers are required")
    @Size(min = 1, message = "At least one seat number must be selected")
    private List<String> seatNumbers;

    @NotNull(message = "Total price cannot be null")
    @DecimalMin(value = "1.0", message = "Price must be greater than 0")
    private BigDecimal totalPrice;

    @NotNull(message = "Booking status cannot be null")
    private BookingStatus status;

    private LocalDateTime bookedAt;
    private LocalDateTime cancelledAt;
}
