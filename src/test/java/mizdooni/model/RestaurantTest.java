package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

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
        assertEquals(null, restaurant.getTable(3));
        assertEquals(table, restaurant.getTable(1));
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

}