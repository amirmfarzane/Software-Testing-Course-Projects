package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

class RestaurantTest {

    private Restaurant restaurant;
    private Address address;
    private User manager;
    private LocalDateTime reviewDateTime1;
    private LocalDateTime reviewDateTime2;
    private String reviewComment;

    @BeforeEach
    void setUp() {
        address = new Address("Enghelab Square", "Tehran", "12345");

        manager = new User("managerUser", "managerPass", "manager@example.com", address, User.Role.manager);

        restaurant = new Restaurant(
                "Shila fastfood",
                manager,
                "Fastfood",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                "Shila fastfood description",
                address,
                "imageLink.jpg"
        );

        reviewDateTime1 = LocalDateTime.of(2024, 10, 25, 19, 0);
        reviewDateTime2 = LocalDateTime.of(2024, 10, 02, 19, 0);
        reviewComment = "Was delicious!";
    }

    @ParameterizedTest
    @DisplayName("Parameterized Test: Adding Tables with Different Seat Numbers")
    @ValueSource(ints = {2, 4, 6, 8})
    public void testAddTableWithDifferentSeats(int seatsNumber) {
        Table table = new Table(0, restaurant.getId(), seatsNumber);
        restaurant.addTable(table);

        assertEquals(1, restaurant.getTables().size());
        assertEquals(1, table.getTableNumber());
        assertEquals(seatsNumber, table.getSeatsNumber());

        restaurant.getTables().clear();
    }

    @Test
    @DisplayName("Test: Checking restaurant get tables fails when table not exists")
    public void testGetTableFindSuccessfully(){
        int seatsNumber = 4;
        Table table = new Table(1, restaurant.getId(), seatsNumber);
        restaurant.addTable(table);
        assertEquals(table, restaurant.getTable(1));
    }

    @Test
    @DisplayName("Test: Checking restaurant get tables return true object")
    public void testGetTableTableNotExists(){
        assertEquals(null, restaurant.getTable(1));
    }

    @Test
    @DisplayName("Test: Getting Maximum Seats Number When No Table exists")
    public void testGetMaxSeatsNumberWhenNotTableExists() {
        int maxSeats = restaurant.getMaxSeatsNumber();
        assertEquals(0, maxSeats);
    }

    @Test
    @DisplayName("Test: Getting Maximum Seats Number")
    public void testGetMaxSeatsNumber() {
        Table table1 = new Table(0, restaurant.getId(), 4);
        Table table2 = new Table(0, restaurant.getId(), 8);
        Table table3 = new Table(0, restaurant.getId(), 6);

        restaurant.addTable(table1);
        restaurant.addTable(table2);
        restaurant.addTable(table3);

        int maxSeats = restaurant.getMaxSeatsNumber();
        assertEquals(8, maxSeats);
    }

    @Test
    @DisplayName("Test: Calculating Average Rating")
    public void testGetAverageRating() {
        User client1 = new User("client1", "clientPass1", "client1@example.com", address, User.Role.client);
        User client2 = new User("client2", "clientPass2", "client2@example.com", address, User.Role.client);

        Rating ratingReview1 = new Rating(4, 5, 3, 4);
        Rating ratingReview2 = new Rating(3, 4, 5, 3);

        Review review1 = new Review(client1, ratingReview1, reviewComment, reviewDateTime1);
        Review review2 = new Review(client2, ratingReview2, reviewComment, reviewDateTime2);

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        Rating averageRating = restaurant.getAverageRating();

        float error_value = 0.01F;
        assertEquals(3.5, averageRating.food, error_value);
        assertEquals(4.5, averageRating.service, error_value);
        assertEquals(4.0, averageRating.ambiance, error_value);
        assertEquals(3.5, averageRating.overall, error_value);
    }

    @Test
    @DisplayName("Test: Getting Star Count Based on Average Rating")
    public void testGetStarCount() {
        User client1 = new User("client1", "clientPass1", "client1@example.com", address, User.Role.client);
        User client2 = new User("client2", "clientPass2", "client2@example.com", address, User.Role.client);

        Rating ratingReview1 = new Rating(4, 5, 3, 4);
        Rating ratingReview2 = new Rating(3, 4, 5, 3);

        Review review1 = new Review(client1, ratingReview1, reviewComment, reviewDateTime1);
        Review review2 = new Review(client2, ratingReview2, reviewComment, reviewDateTime2);

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        int starCount = restaurant.getStarCount();
        assertEquals(4, starCount);
    }

    @ParameterizedTest
    @DisplayName("Parameterized Test: Adding and Updating Review for Restaurant")
    @CsvSource({
            "5, 5, 5, 5, 1, 1, 1, 1",
            "3, 2, 4, 5, 5, 5, 5, 5",
            "1, 1, 1, 1, 5, 2, 3, 1",
            "5, 4, 3, 2, 2, 3, 4, 5",
            "2, 3, 2, 4, 2, 3, 2, 4",
            "4, 3, 5, 2, 4, 3, 5, 1"
    })

    public void testAddAndUpdateReview(int initialFood, int initialService, int initialAmbiance, int initialOverall,
                                       int updatedFood, int updatedService, int updatedAmbiance, int updatedOverall) {
        User client = new User("clientUser", "clientPass", "client@example.com", address, User.Role.client);
        Rating initialRating = new Rating(initialFood, initialService, initialAmbiance, initialOverall);

        Review initialReview = new Review(client, initialRating, reviewComment, reviewDateTime1);
        restaurant.addReview(initialReview);

        assertEquals(1, restaurant.getReviews().size());
        assertEquals(initialReview, restaurant.getReviews().get(0));

        Rating updatedRating = new Rating(updatedFood, updatedService, updatedAmbiance, updatedOverall);
        Review updatedReview = new Review(client, updatedRating, reviewComment, reviewDateTime2);
        restaurant.addReview(updatedReview);

        assertEquals(1, restaurant.getReviews().size());
        assertEquals(updatedReview, restaurant.getReviews().get(0));

        updatedRating = restaurant.getReviews().get(0).getRating();
        assertEquals(updatedFood, updatedRating.food);
        assertEquals(updatedService, updatedRating.service);
        assertEquals(updatedAmbiance, updatedRating.ambiance);
        assertEquals(updatedOverall, updatedRating.overall);
    }

}