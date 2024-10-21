package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

class RestaurantTest {

    private Restaurant restaurant;
    private Address address;
    private User manager;

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
    }

    @Test
    @DisplayName("Test: Adding a Table to Restaurant")
    public void testAddTable() {
        Table table = new Table(3, restaurant.getId(), 4);
        restaurant.addTable(table);

        assertEquals(1, restaurant.getTables().size());
        assertEquals(1, table.getTableNumber());
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
        LocalDateTime reviewDateTime1 = LocalDateTime.of(2024, 10, 25, 19, 0);
        LocalDateTime reviewDateTime2 = LocalDateTime.of(2024, 10, 02, 19, 0);

        Review review1 = new Review(client1, ratingReview1, "Was delicious!", reviewDateTime1);
        Review review2 = new Review(client2, ratingReview2, "Was so delicious!", reviewDateTime2);

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
        LocalDateTime reviewDateTime1 = LocalDateTime.of(2024, 10, 25, 19, 0);
        LocalDateTime reviewDateTime2 = LocalDateTime.of(2024, 10, 02, 19, 0);

        Review review1 = new Review(client1, ratingReview1, "Was delicious!", reviewDateTime1);
        Review review2 = new Review(client2, ratingReview2, "Was so delicious!", reviewDateTime2);

        restaurant.addReview(review1);
        restaurant.addReview(review2);

        int starCount = restaurant.getStarCount();
        assertEquals(4, starCount);
    }

}