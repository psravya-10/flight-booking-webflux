package com.flightapp.model;

import com.flightapp.model.enums.AirlineName;
import com.flightapp.model.enums.PlaceName;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightModelTest {

    @Test
    void testFlightSettersAndGetters() {
        Flight f = new Flight();
        f.setId("F1");
        f.setAirlineName(AirlineName.INDIGO);
        f.setFromPlace(PlaceName.HYDERABAD);
        f.setToPlace(PlaceName.DELHI);
        f.setDepartureDate(LocalDate.now());
        f.setDepartureTime(LocalTime.NOON);
        f.setTotalSeats(50);
        f.setOneWayPrice(BigDecimal.valueOf(5000));

        assertEquals("F1", f.getId());
        assertEquals(AirlineName.INDIGO, f.getAirlineName());
    }
}
