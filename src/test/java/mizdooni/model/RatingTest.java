package mizdooni.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RatingTest {

    @ParameterizedTest
    @DisplayName("Parameterized Test: Checking Get Star Count for Different Overall Ratings")
    @CsvSource({
            "4.2, 4",
            "5.0, 5",
            "6.7, 5",
            "4.0, 4",
            "3.9, 4",
            "5.5, 5",
            "2.5, 3",
            "4.8, 5",
            "1.0, 1"
    })
    public void testGetStarCount(double overall, int expectedStarCount) {
        Rating rating = new Rating(4.0, 3.5, 4.2, overall);
        assertEquals(expectedStarCount, rating.getStarCount());
    }
}
