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

        manager = new User("managerUser", "managerPass", "manager@example.com", address, User.Role.manager);

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
    @DisplayName("Test: Checking if Table is Reserved at Specific Time")
    public void testIsReserved() {
        LocalDateTime reservedDateTime = LocalDateTime.of(2024, 10, 25, 19, 0);
        LocalDateTime nonReservedDateTime = LocalDateTime.of(2024, 10, 26, 19, 0);

        User client = new User("clientUser", "clientPass", "client@example.com", address, User.Role.client);
        Reservation reservation = new Reservation(client, restaurant, table, reservedDateTime);
        table.addReservation(reservation);

        assertTrue(table.isReserved(reservedDateTime));

        assertFalse(table.isReserved(nonReservedDateTime));
    }
}
