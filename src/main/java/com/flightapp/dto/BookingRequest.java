package com.flightapp.dto;

import java.util.List;

import com.flightapp.model.PassengerInfo;
import com.flightapp.model.enums.MealType;
import com.flightapp.model.enums.TripType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookingRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least one seat must be booked")
    private Integer numberOfSeats;

    @Valid
    @NotNull(message = "Passengers list cannot be null")
    @Size(min = 1, message = "At least one passenger is required")
    private List<PassengerInfo> passengers;

    @NotNull(message = "Meal type must be selected")
    private MealType mealType;

    @NotNull(message = "Seat numbers must be provided")
    @Size(min = 1, message = "At least one seat number is required")
    private List<String> seatNumbers;

    @NotNull(message = "Trip type required")
    private TripType tripType;

	
}
