package mizdooni.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RatingTest {

    @Test
    @DisplayName("Test: Get Start Count")
    public void testGetStarCountOutOfFive() {
        Rating rating = new Rating(1,2,3, 7);
        assertEquals(5, rating.getStarCount());
    }
}
