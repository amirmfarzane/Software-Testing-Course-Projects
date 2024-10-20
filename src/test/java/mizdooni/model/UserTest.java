package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
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
        address = new Address("Engela Square ", "Tehran", "12345");

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
}
