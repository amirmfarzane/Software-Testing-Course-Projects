package mizdooni.controllers;

import static mizdooni.controllers.ControllerUtils.PARAMS_BAD_TYPE;
import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import mizdooni.model.Address;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;

@ExtendWith(MockitoExtension.class)
public class ReservationControllerTest {
    @Mock
    RestaurantService restaurantService;

    @Mock
    ReservationService reservationService;

    @InjectMocks
    ReservationController reservationController;

    private Restaurant restaurant;
    private Address address;
    private User manager;
    private User customer;


    @BeforeEach
    public void setup(){
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

        customer = new User("clientUser", "clientPass", "client@example.com", address, User.Role.client);

    }

    @Test
    void testGetReservations_Success() {
        int tableId = 5;
        Table table = new Table(tableId,restaurant.getId(),4);
        restaurant.addTable(table);
        String date = "2023-11-01";
        LocalDate localDate = LocalDate.parse(date, ControllerUtils.DATE_FORMATTER);
        // customer.addReservation(reservation);

        List<Reservation> reservations = Arrays.asList(
            new Reservation(any(User.class), any(Restaurant.class), any(Table.class), any(LocalDateTime.class)),
            new Reservation(any(User.class), any(Restaurant.class), any(Table.class), any(LocalDateTime.class)));
        
        try {
            when(reservationService.getReservations(restaurant.getId(), tableId, localDate)).thenReturn(reservations);
            Response response = reservationController.getReservations(restaurant.getId(), tableId, date);
            assertEquals("restaurant table reservations", response.getMessage());
            assertEquals(reservations, response.getData());
        } catch (Exception e) {
            assertEquals("restaurant table reservations", e.getMessage());
        }
    }

    @Test
    void testGetReservations_InvalidDateFormat() {
        int restaurantId = 1;
        int table = 5;
        String date = "invalid-date";
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getReservations(restaurantId, table, date));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testGetCustomerReservations_Success() {
        List<Reservation> reservations = Arrays.asList(
            new Reservation(any(User.class), any(Restaurant.class), any(Table.class), any(LocalDateTime.class)),
            new Reservation(any(User.class), any(Restaurant.class), any(Table.class), any(LocalDateTime.class)));
        try{
            when(reservationService.getCustomerReservations(customer.getId())).thenReturn(reservations);
            Response response = reservationController.getCustomerReservations(customer.getId());
            assertEquals("user reservations", response.getMessage());
            assertEquals(reservations, response.getData());
        }
        catch(Exception e){
            assertEquals("user reservations", e.getMessage());

        }
    }

    @Test
    void testGetAvailableTimes_Success() {
        int people = 4;
        String date = "2023-11-01";
        LocalDate localDate = LocalDate.parse(date, ControllerUtils.DATE_FORMATTER);

        List<LocalTime> availableTimes = Arrays.asList(LocalTime.of(12, 0), LocalTime.of(13, 0));
        try {
            when(restaurantService.getRestaurant(restaurant.getId())).thenReturn(restaurant);
            when(reservationService.getAvailableTimes(restaurant.getId(), people, localDate)).thenReturn(availableTimes);

            Response response = reservationController.getAvailableTimes(restaurant.getId(), people, date);

            assertEquals("available times", response.getMessage());
            assertEquals(availableTimes, response.getData());
        } catch (Exception e) {
            assertEquals("available times", e.getMessage());
        }
    }

    @Test
    void testGetAvailableTimes_InvalidDateFormat() {
        int restaurantId = 1;
        int people = 4;
        String date = "invalid-date";
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.getAvailableTimes(restaurantId, people, date));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testAddReservation_Success() {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "2023-11-01T18:00");

        int people = 4;
        LocalDateTime datetime = LocalDateTime.parse("2023-11-01T18:00", ControllerUtils.DATETIME_FORMATTER);

        Reservation reservation = new Reservation(manager, restaurant, new Table(12, restaurantId, 5), datetime);
        try {
            when(reservationService.reserveTable(restaurantId, people, datetime)).thenReturn(reservation);
            Response response = reservationController.addReservation(restaurantId, params);
            assertEquals("reservation done", response.getMessage());
            assertEquals(reservation, response.getData());
            
        } catch (Exception e) {
            assertEquals("reservation done", e.getMessage());
        }
    }

    @Test
    void testAddReservation_MissingParams() {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.addReservation(restaurantId, params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    void testAddReservation_InvalidDateFormat() {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "invalid-datetime");

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.addReservation(restaurantId, params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    void testCancelReservation_Success() {
        int reservationNumber = 123;

        when(reservationController.cancelReservation(reservationNumber)).thenReturn(null)
        Response response = reservationController.cancelReservation(reservationNumber);

        assertEquals("reservation cancelled", response.getMessage());
        verify(reservationService, times(1)).cancelReservation(reservationNumber);
    }

    @Test
    void testCancelReservation_Exception() {
        int reservationNumber = 123;
        // doThrow(new RuntimeException("error")).when(reservationService).cancelReservation(reservationNumber);

        ResponseException exception = assertThrows(ResponseException.class,
                () -> reservationController.cancelReservation(reservationNumber));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("error", exception.getMessage());
    }
}
