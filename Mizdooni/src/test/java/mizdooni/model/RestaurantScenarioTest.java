package mizdooni.model;

import io.cucumber.java.en.*;
import mizdooni.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.lang.String;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CucumberContextConfiguration
public class RestaurantScenarioTest {
    private Restaurant restaurant;
    private Address address;
    private User client1;
    private User client2;

    @Given("a restaurant exists")
    public void aRestaurantExists() {
        address = new Address("789 Maple Ave", "Tehran", "45678");
        User manager = new User("managerUser", "managerPass", "manager@example.com", address, User.Role.manager);
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

    @And("the following reviews are added to the restaurant:")
    public void theFollowingReviewsAreAddedToTheRestaurant(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            String username = row.get("user");
            float food = Float.parseFloat(row.get("food"));
            float service = Float.parseFloat(row.get("service"));
            float ambiance = Float.parseFloat(row.get("ambiance"));
            float overall = Float.parseFloat(row.get("overall"));

            User client = new User(username, "password", username + "@example.com", address, User.Role.client);
            Review review = new Review(client, new Rating(food, service, ambiance, overall), "Good review!", null);
            restaurant.addReview(review);
        });
    }

    @When("I calculate the average rating")
    public void iCalculateTheAverageRating() {
        // Average rating is implicitly calculated inside `Restaurant` class methods
    }

    @Then("the average food rating should be {double}")
    public void theAverageFoodRatingShouldBe(Double expectedFoodRating) {
        assertEquals(expectedFoodRating, restaurant.getAverageRating().food, 0.01);
    }

    @Then("the average service rating should be {double}")
    public void theAverageServiceRatingShouldBe(Double expectedServiceRating) {
        assertEquals(expectedServiceRating, restaurant.getAverageRating().service, 0.01);
    }

    @Then("the average ambiance rating should be {double}")
    public void theAverageAmbianceRatingShouldBe(Double expectedAmbianceRating) {
        assertEquals(expectedAmbianceRating, restaurant.getAverageRating().ambiance, 0.01);
    }

    @Then("the average overall rating should be {double}")
    public void theAverageOverallRatingShouldBe(Double expectedOverallRating) {
        assertEquals(expectedOverallRating, restaurant.getAverageRating().overall, 0.01);
    }

    @When("another review is added with the same user:")
    public void anotherReviewIsAddedWithTheSameUser(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            String username = row.get("user");
            float food = Float.parseFloat(row.get("food"));
            float service = Float.parseFloat(row.get("service"));
            float ambiance = Float.parseFloat(row.get("ambiance"));
            float overall = Float.parseFloat(row.get("overall"));

            User client = new User(username, "password", username + "@example.com", address, User.Role.client);
            Review review = new Review(
                    client,
                    new Rating(food, service, ambiance, overall),
                    "Updated review comment",
                    LocalDateTime.now()
            );
            restaurant.addReview(review);
        });
    }

    @Then("the restaurant should contain only the updated review")
    public void theRestaurantShouldContainOnlyTheUpdatedReviewFor(String username, io.cucumber.datatable.DataTable dataTable) {
        var expectedRow = dataTable.asMaps().get(0);
        var updatedReview = restaurant.getReviews().stream()
                .filter(review -> review.getUser().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedReview, "The updated review should exist in the list");

        assertEquals(Float.parseFloat(expectedRow.get("food")), updatedReview.getRating().food, 0.01);
        assertEquals(Float.parseFloat(expectedRow.get("service")), updatedReview.getRating().service, 0.01);
        assertEquals(Float.parseFloat(expectedRow.get("ambiance")), updatedReview.getRating().ambiance, 0.01);
        assertEquals(Float.parseFloat(expectedRow.get("overall")), updatedReview.getRating().overall, 0.01);
    }


}
