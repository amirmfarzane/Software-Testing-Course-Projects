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

    @Given("clients exist")
    public void clientsExist(){
        client1 = new User("client1", "client1Pass", "client1@example.com", address, User.Role.client);
        client2 = new User("client2", "client2Pass", "client2@example.com", address, User.Role.client);
    }

    @Given("a restaurant exists")
    public void aRestaurantExists() {
        address = new Address("Enghelab square", "Tehran", "45678");
        restaurant = new Restaurant(
                "Shila fastfood",
                client1,
                "Fastfood",
                LocalTime.of(9, 0),
                LocalTime.of(23, 0),
                "Shila fastfood description",
                address,
                "imageLink.jpg"
        );
    }

    @When("Calculate the average rating")
    public void calculateTheAverageRating() {
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

    @And("the following reviews are added to the restaurant:")
    public void theFollowingReviewsAreAddedToTheRestaurant(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            float food = Float.parseFloat(row.get("food"));
            float service = Float.parseFloat(row.get("service"));
            float ambiance = Float.parseFloat(row.get("ambiance"));
            float overall = Float.parseFloat(row.get("overall"));
            String client = row.get("user");


            if(client.equals("client1")) {
                Review review = new Review(client1, new Rating(food, service, ambiance, overall), "Good review!", null);
                restaurant.addReview(review);
            }
            else if(client.equals("client2")){
                Review review = new Review(client2, new Rating(food, service, ambiance, overall), "Good review!", null);
                restaurant.addReview(review);
            }
        });
    }

    @When("another review is added with the same user:")
    public void anotherReviewIsAddedWithTheSameUser(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            float food = Float.parseFloat(row.get("food"));
            float service = Float.parseFloat(row.get("service"));
            float ambiance = Float.parseFloat(row.get("ambiance"));
            float overall = Float.parseFloat(row.get("overall"));

            Review review = new Review(
                    client1,
                    new Rating(food, service, ambiance, overall),
                    "Updated review comment",
                    LocalDateTime.now()
            );
            restaurant.addReview(review);
        });
    }

    @Then("the restaurant should contain only the last review for {string}:")
    public void theRestaurantShouldContainOnlyTheLastReviewFor(String username, io.cucumber.datatable.DataTable dataTable) {
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

    @When("no review added")
    public void noReviewAdded(){
    }

    @Then("average rating is zero")
    public void averageRatingIsZero() {
        assertEquals(0, restaurant.getAverageRating().food, 0.01);
        assertEquals(0, restaurant.getAverageRating().overall, 0.01);
        assertEquals(0, restaurant.getAverageRating().service, 0.01);
        assertEquals(0, restaurant.getAverageRating().ambiance, 0.01);
    }

}
