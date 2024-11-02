package mizdooni.controllers;

import mizdooni.model.*;
import mizdooni.response.PagedList;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.RestaurantService;
import mizdooni.service.ReviewService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private Restaurant restaurant;
    private Address address;
    private User manager;
    private User customer;
    private User customer_2;

    @BeforeEach
    public void setup() {
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

        customer = new User("customerUser", "customerPass", "customer@example.com", address, User.Role.client);
        customer_2 = new User("customerUser2", "customerPass2", "customer2@example.com", address, User.Role.client);
    }

    @Test
    @DisplayName("Test: Get Reviews for Restaurant - Success")
    void testGetReviews_Success() throws Exception {
        int page = 1;

        Rating rating = new Rating(4.0, 4.5, 4.2, 4.3);
        String comment1 = "Excellent food and ambiance!";
        String comment2 = "Good service but could be better.";
        LocalDateTime datetime1 = LocalDateTime.of(2023, 11, 1, 12, 0);
        LocalDateTime datetime2 = LocalDateTime.of(2023, 11, 2, 14, 30);

        Review review1 = new Review(customer, rating, comment1, datetime1);
        Review review2 = new Review(customer_2, rating, comment2, datetime2);

        List<Review> reviewList = Arrays.asList(review1, review2);
        PagedList<Review> pagedReviews = new PagedList<>(reviewList, page, reviewList.size());

        when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
        when(reviewService.getReviews(restaurant.getId(), page)).thenReturn(pagedReviews);

        Response response = reviewController.getReviews(restaurant.getId(), page);

        assertNotNull(response);
        assertEquals("reviews for restaurant (" + restaurant.getId() + "): " + restaurant.getName(), response.getMessage());
        assertEquals(pagedReviews, response.getData());
        verify(reviewService).getReviews(restaurant.getId(), page);
    }

    @Test
    @DisplayName("Test: Get Reviews for Restaurant - Restaurant Not Found")
    void testGetReviews_RestaurantNotFound() throws Exception {
        int page = 1;

        when(restaurantService.getRestaurant(restaurant.getId()))
                .thenThrow(new ResponseException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.getReviews(restaurant.getId(), page));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Restaurant not found", exception.getMessage());
        verify(reviewService, never()).getReviews(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Test: Add Review to Restaurant - Success")
    void testAddReview_Success() throws Exception {
        int restaurantId = restaurant.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food and ambiance!");

        Map<String, Number> ratingMap = new HashMap<>();
        ratingMap.put("food", 4.5);
        ratingMap.put("service", 5);
        ratingMap.put("ambiance", 4.2);
        ratingMap.put("overall", 4.8);
        params.put("rating", ratingMap);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        doNothing().when(reviewService).addReview(eq(restaurantId), any(Rating.class), anyString());

        Response response = reviewController.addReview(restaurantId, params);

        assertNotNull(response);
        assertEquals("review added successfully", response.getMessage());
        verify(reviewService).addReview(eq(restaurantId), any(Rating.class), eq("Great food and ambiance!"));
    }

    @Test
    @DisplayName("Test: Add Review to Restaurant - Missing Parameters")
    void testAddReview_MissingParameters() throws Exception {
        int restaurantId = restaurant.getId();
        Map<String, Object> params = new HashMap<>();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.addReview(restaurantId, params));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("parameters missing", exception.getMessage());
        verify(reviewService, never()).addReview(anyInt(), any(Rating.class), anyString());
    }

    @Test
    @DisplayName("Test: Add Review to Restaurant - Invalid Parameter Types")
    void testAddReview_InvalidParameterTypes() throws Exception {
        int restaurantId = restaurant.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Great food and ambiance!");

        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("food", "Excellent"); // Invalid type
        params.put("rating", ratingMap);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.addReview(restaurantId, params));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("bad parameter type", exception.getMessage());
        verify(reviewService, never()).addReview(anyInt(), any(Rating.class), anyString());
    }

    @ParameterizedTest
    @CsvSource({
            //null
            "null, 4.0, 3.0, 4.5",
            "4.0, null, 3.0, 4.5",
            "4.0, 4.0, null, 4.5",
            "4.0, 4.0, 3.0, null",
            //string
            "invalid, 4.0, 3.0, 4.5",
            "4.0, invalid, 3.0, 4.5",
            "4.0, 4.0, invalid, 4.5",
            "4.0, 4.0, 3.0, invalid"
    })
    @DisplayName("Test: Add Review to Restaurant - Invalid Rating Values")
    void testAddReview_InvalidRatingValues(String foodStr, String serviceStr, String ambianceStr, String overallStr) throws Exception {
        int restaurantId = restaurant.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("comment", "Review with invalid ratings.");

        Map<String, Number> ratingMap = new HashMap<>();
        Double food = parseDoubleOrNull(foodStr);
        Double service = parseDoubleOrNull(serviceStr);
        Double ambiance = parseDoubleOrNull(ambianceStr);
        Double overall = parseDoubleOrNull(overallStr);

        if (food != null) ratingMap.put("food", food);
        if (service != null) ratingMap.put("service", service);
        if (ambiance != null) ratingMap.put("ambiance", ambiance);
        if (overall != null) ratingMap.put("overall", overall);

        params.put("rating", ratingMap);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

        ResponseException exception = assertThrows(ResponseException.class, () -> reviewController.addReview(restaurantId, params));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("bad parameter type", exception.getMessage());
        verify(reviewService, never()).addReview(anyInt(), any(Rating.class), anyString());
    }

    private Double parseDoubleOrNull(String value) {
        if ("null".equals(value)) {
            return null;
        } else {
            return Double.valueOf(value);
        }
    }
}
