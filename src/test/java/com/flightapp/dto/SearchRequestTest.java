package com.flightapp.dto;

import com.flightapp.model.enums.PlaceName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SearchRequestTest {

    @Test
    void testDTO() {
        SearchRequest req = new SearchRequest();
        req.setFromPlace(PlaceName.HYDERABAD);
        req.setToPlace(PlaceName.DELHI);
        req.setJourneyDate(LocalDate.now());

        assertNotNull(req.getFromPlace());
        assertNotNull(req.getToPlace());
        assertNotNull(req.getJourneyDate());
    }
}
