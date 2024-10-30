package mizdooni.controllers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import mizdooni.database.Database;
import mizdooni.exceptions.RestaurantNotFound;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.response.Response;
import mizdooni.service.RestaurantService;
import mizdooni.service.ServiceUtils;
import mizdooni.service.TableService;

@ExtendWith(MockitoExtension.class)
public class TableControllerTest {
    @Mock
    RestaurantService restaurantService;

    @Mock
    TableService tableService;

    @InjectMocks
    TableController tableController;

    private List<Table> mockTables;
    private int restaurantId = 1;

    @BeforeAll
    public void setup(){
        restaurantId = 1;
        mockTables = Collections.singletonList(new Table(1, restaurantId, 4));
    }

    @BeforeEach
    public void runMockito(){
        restaurantId = 1;
        mockTables = Collections.singletonList(new Table(1, restaurantId, 4));
    }


    @Test
    void testGetTables_success() throws RestaurantNotFound{

        Restaurant mockRestaurant = Mockito.mock(Restaurant.class);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(mockRestaurant);

        // doReturn(mockRestaurant).when(tableService).getTables(restaurantId);
        when(tableService.getTables(restaurantId)).thenReturn(mockTables);
        Response response = tableController.getTables(restaurantId);

        assertEquals("tables listed", response.getMessage());
        assertEquals(mockTables, response.getData());
        verify(tableService).getTables(restaurantId);
    }

    // @Test
    // void testGetTables_restaurantNotFound() {
    //     int restaurantId = 1;
        
    //     // Mock a restaurant not found case
    //     doThrow(new ResponseException(HttpStatus.NOT_FOUND, "Restaurant not found"))
    //             .when(restaurantService).checkRestaurant(restaurantId);
        
    //     // Assert exception is thrown
    //     ResponseException exception = assertThrows(ResponseException.class, () -> 
    //         tableController.getTables(restaurantId)
    //     );
        
    //     // Validate
    //     assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    //     assertEquals("Restaurant not found", exception.getReason());
    // }

    // @Test
    // void testAddTable_success() {
    //     int restaurantId = 1;
    //     Map<String, String> params = new HashMap<>();
    //     params.put("seatsNumber", "4");
        
    //     // Mock the dependencies
    //     doNothing().when(restaurantService).checkRestaurant(restaurantId);
    //     doNothing().when(tableService).addTable(restaurantId, 4);
        
    //     // Call the method
    //     Response response = tableController.addTable(restaurantId, params);
        
    //     // Validate
    //     assertEquals("table added", response.getMessage());
    //     verify(restaurantService).checkRestaurant(restaurantId);
    //     verify(tableService).addTable(restaurantId, 4);
    // }

    // @Test
    // void testAddTable_missingSeatsNumber() {
    //     int restaurantId = 1;
    //     Map<String, String> params = new HashMap<>();
        
    //     // Mock restaurant validation
    //     doNothing().when(restaurantService).checkRestaurant(restaurantId);

    //     // Assert exception for missing parameter
    //     ResponseException exception = assertThrows(ResponseException.class, () -> 
    //         tableController.addTable(restaurantId, params)
    //     );
        
    //     // Validate
    //     assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    //     assertEquals("Missing required parameters", exception.getReason());
    // }

    // @Test
    // void testAddTable_invalidSeatsNumber() {
    //     int restaurantId = 1;
    //     Map<String, String> params = new HashMap<>();
    //     params.put("seatsNumber", "invalid");

    //     // Mock restaurant validation
    //     doNothing().when(restaurantService).checkRestaurant(restaurantId);

    //     // Assert exception for invalid seat number
    //     ResponseException exception = assertThrows(ResponseException.class, () -> 
    //         tableController.addTable(restaurantId, params)
    //     );
        
    //     // Validate
    //     assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    //     assertEquals("Invalid parameter type", exception.getReason());
    // }

    // @Test
    // @DisplayName("")
    // public void addTableFailWhenSeatsNumberNegativeTest(){
    //     table = new Table(1, restaurant.getId(), 0);
    //     tableController.addTable(restaurant.getId(), null)

    // }
}
