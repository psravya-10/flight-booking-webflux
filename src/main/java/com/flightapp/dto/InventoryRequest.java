package com.flightapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.flightapp.model.enums.AirlineName;
import com.flightapp.model.enums.PlaceName;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequest {

    @NotNull(message = "Airline name cannot be null")
    private AirlineName airlineName;

    private String airlineLogo;

    @NotNull(message = "From place cannot be null")
    private PlaceName fromPlace;

    @NotNull(message = "To place cannot be null")
    private PlaceName toPlace;

    @NotNull(message = "Departure date cannot be null")
    private LocalDate departureDate;

    @NotNull(message = "Departure time cannot be null")
    private LocalTime departureTime;

   
    private LocalDate returnDate;
    private LocalTime returnTime;

    @NotNull(message = "One-way price cannot be null")
    @DecimalMin(value = "1.0", message = "price should be greater than 0")
    private BigDecimal oneWayPrice;

    @DecimalMin(value = "1.0", message = "Round trip price should be greater than 0")
    private BigDecimal roundTripPrice;

    @NotNull(message = "Total seats cannot be null")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;
}
