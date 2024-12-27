package mizdooni.model;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class UserScenarioTest {

    Address address;
    User user;
    Restaurant restaurant;
    Reservation reservation;

    @Given("a restaurant named {string} with a table for {int} exists")
    public void aRestaurantExists(String restaurantName, int tableSeats) {
        address = new Address("Enghelab Square", "Tehran", "12345");
        User manager = new User("managerUser", "managerPass", "manager@example.com", address, User.Role.manager);
        restaurant = new Restaurant(
                restaurantName,
                manager,
                "Fastfood",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                restaurantName,
                address,
                "imageLink.jpg"
        );
        Table table = new Table(1, restaurant.getId(), tableSeats);
        restaurant.addTable(table);
    }

    @Given("a user named {string} exists")
    public void aUserExists(String userName) {
        user = new User(userName, "userPass", userName + "@example.com", address, User.Role.client);
    }

    @When("the user makes a reservation on {string}")
    public void theUserMakesAReservation(String dateTime) {
        LocalDateTime reservationDateTime = LocalDateTime.parse(dateTime);
        Table table = restaurant.getTables().get(0); // Assumes there's at least one table
        reservation = new Reservation(user, restaurant, table, reservationDateTime);
        user.addReservation(reservation);
    }

    @Then("the user should have {int} reservation")
    public void theUserShouldHaveReservation(int reservationCount) {
        assertEquals(reservationCount, user.getReservations().size());
    }

    @Then("the reservation number should be {int}")
    public void theReservationNumberShouldBe(int reservationNumber) {
        assertEquals(reservationNumber, reservation.getReservationNumber());
    }

    @Given("a user named {string} has a reservation at {string} on {string}")
    public void aUserHasAReservation(String userName, String restaurantName, String dateTime) {
        aRestaurantExists(restaurantName, 4);
        aUserExists(userName);
        theUserMakesAReservation(dateTime);
    }
    @When("the user reserve for the restaurant")
    public void theUserReservesForTheRestaurant() {
        assertTrue(user.checkReserved(restaurant), "The user should have a reservation for shila fast food" );
    }

    @When("the result of user checking reservations for the restaurant must be false")
    public void theUserChecksReservationsForTheRestaurantIsFalse() {
        assertFalse(user.checkReserved(restaurant), "The user should have a reservation for shila fast food" );
    }

    @When("the result of user checking reservations for the restaurant must be true")
    public void theUserChecksReservationsForTheRestaurantIsTrue() {
        assertTrue(user.checkReserved(restaurant), "The user should have a reservation for shila fast food" );
    }

    @When("the result of user checking reservations for another restaurant must be false")
    public void theUserChecksReservationsForAnotherRestaurant() {
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
        assertFalse(user.checkReserved(anotherRestaurant), "The user should have a reservation for italian restaurant");
    }


    @Then("the result should be true")
    public void theResultShouldBeTrue() {
        assertTrue(user.checkReserved(restaurant));
    }

    @Then("the result should be false")
    public void theResultShouldBeFalse() {
        assertFalse(user.checkReserved(restaurant));
    }

    @When("the reservation is cancelled")
    public void theReservationIsCancelled() {
        reservation.cancel();
    }

    @When("the user makes a reservation with an offset of {int} days from today")
    public void theUserMakesAReservation(int daysOffset) {
        LocalDateTime reservationDateTime = LocalDateTime.now().plusDays(daysOffset);
        Table table = restaurant.getTables().get(0);
        reservation = new Reservation(user, restaurant, table, reservationDateTime);
        user.addReservation(reservation);
    }

    @Then("the result should be {string}")
    public void theResultShouldBe(String expectedResult) {
        boolean actualResult = user.checkReserved(restaurant);
        assertEquals(Boolean.parseBoolean(expectedResult), actualResult);
    }
}
