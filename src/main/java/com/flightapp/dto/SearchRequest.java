package com.flightapp.dto;

import java.time.LocalDate;

import com.flightapp.model.enums.PlaceName;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchRequest {

    @NotNull(message = "From place cannot be null")
    private PlaceName fromPlace;

    @NotNull(message = "To place cannot be null")
    private PlaceName toPlace;

    @NotNull(message = "Journey date cannot be null")
    @FutureOrPresent(message = "Journey date cannot be in the past")
    private LocalDate journeyDate;

    
    private boolean roundTrip;
}
