package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

class TableTest {

    private Table table;
    private Address address;
    private Restaurant restaurant;
    private User manager;

    @BeforeEach
    void setUp() {
        address = new Address("Enghelab Square", "Tehran", "12345");

        // Creating a manager user for the restaurant
        manager = new User("managerUser", "managerPass", "manager@example.com", address, User.Role.manager);

        // Setting up a restaurant with the manager
        restaurant = new Restaurant(
                "Shila fastfood",
                manager,
                "Fastfood",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                "Shila fastfood",
                address,
                "imageLink.jpg"
        );

        
        table = new Table(1, restaurant.getId(), 4);
        restaurant.addTable(table);
    }

    @Test
    @DisplayName("Test: Adding Reservation to Table")
    public void testAddReservation() {

        LocalDateTime reservationDateTime = LocalDateTime.of(2024, 10, 25, 19, 0);
        User client = new User("clientUser", "clientPass", "client@example.com", address, User.Role.client);
        Reservation reservation = new Reservation(client, restaurant, table, reservationDateTime);

        table.addReservation(reservation);
        assertEquals(1, table.getReservations().size());
        assertEquals(reservation, table.getReservations().get(0));
    }


}