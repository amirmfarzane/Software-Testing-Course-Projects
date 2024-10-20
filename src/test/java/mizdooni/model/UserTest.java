package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

class UserTest {

    private User user;
    private Address address;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        address = new Address("Enghelab Square", "Tehran", "12345");

        User manager = new User("managerUser", "managerPass", "manager@example.com", address, User.Role.manager);

        restaurant = new Restaurant(
                "Test Restaurant",
                manager,
                "Fastfood",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                "Shila fastfood",
                address,
                "imageLink.jpg"
        );

        user = new User("testUser", "userPass", "user@example.com", address, User.Role.client);
    }

    @Test
    @DisplayName("Test: Adding Reservation to User")
    public void testAddReservation() {
        Table table = new Table(1, restaurant.getId(), 4);
        restaurant.addTable(table);

        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 25, 19, 0);
        Reservation reservation = new Reservation(user, restaurant, table, reservationDateTime);

        user.addReservation(reservation);
        assertEquals(1, user.getReservations().size());
        assertEquals(0, reservation.getReservationNumber());
    }

    @Test
    @DisplayName("Test: Checking if User Has Reserved a Restaurant")
    public void testCheckReserved() {
        Table table = new Table(1, restaurant.getId(), 4);
        restaurant.addTable(table);

        LocalDateTime pastDateTime = LocalDateTime.of(2024, 10, 15, 18, 0);
        Reservation reservation = new Reservation(user, restaurant, table, pastDateTime);

        user.addReservation(reservation);

        assertTrue(user.checkReserved(restaurant));

        Restaurant anotherRestaurant = new Restaurant(
                "Italian Restaurant",
                new User("anotherManager", "anotherPass", "another@example.com", address, User.Role.manager),
                "Italian",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                "Italian dining experience",
                address,
                "imageLink2.jpg"
        );

        assertFalse(user.checkReserved(anotherRestaurant));
    }

}
