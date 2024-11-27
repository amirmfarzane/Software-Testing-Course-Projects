package mizdooni.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import mizdooni.MizdooniApplication;
import mizdooni.model.Address;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;
import mizdooni.service.RestaurantService;
import mizdooni.service.TableService;


@AutoConfigureMockMvc
@SpringBootTest(classes = MizdooniApplication.class)
class TableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private TableService tableService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private TableController tableController;

    Restaurant restaurant;
    Address address;
    Table table1, table2;

    @BeforeEach
    public void setup() {
        User user = new User("test", "test123", "test@gmail.com", null, User.Role.manager);
        address = new Address("Enghelab Square", "Tehran", "12345");
        restaurant = new Restaurant("Kababi", user, "Iranian", LocalTime.of(0, 0),
            LocalTime.of(23, 59), "Tehran", null , "image");
        table1 = new Table(1, 1, 4);
        table2 = new Table(2, 1, 3);

        restaurant.addTable(table1);
        restaurant.addTable(table2);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Given_restaurantIdValid_When_GetTable_Then_returnTableList() throws Exception {
        int restaurantId = restaurant.getId();
        List<Table> mockTables = Arrays.asList(table1, table2);

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(tableService.getTables(restaurantId)).thenReturn(mockTables);

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("tables listed"))
            .andExpect(jsonPath("$.data[0].tableNumber").value(1))
            .andExpect(jsonPath("$.data[1].tableNumber").value(2))
            .andExpect(jsonPath("$.data[0].seatsNumber").value(4))
            .andExpect(jsonPath("$.data[1].seatsNumber").value(3));
    }

    @Test
    void Given_restaurantIdIsValid_When_TableListIsEmpty_Then_EmptyList() throws Exception{
        int restaurantId = restaurant.getId();

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(tableService.getTables(restaurantId)).thenReturn(null);

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("tables listed"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void Given_InvalidRestaurantId_When_GetTables_Then_returnException() throws Exception{
        int restaurantId = 99;

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(null);

        mockMvc.perform(get("/tables/{restaurantId}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    
    }


    @Test
    void Given_AllThingsOk_When_AddingTable_Then_Add() throws Exception {
    int restaurantId = restaurant.getId();
    String requestBody = objectMapper.writeValueAsString(Collections.singletonMap("seatsNumber", "4"));

    when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

    mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("table added"));
    }

    @Test
    void Given_MissSeatNumber_When_AddingTable_Then_Fails() throws Exception {
        int restaurantId = restaurant.getId();
        String requestBody = "{}";

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);

        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("parameters missing"));
    }

    @ParameterizedTest
    @CsvSource(value = {"as", "2.3"})
    void Given_InvalidSeatNumber_When_AddingTable_Then_Fails(String seatsNumber) throws Exception {
        int restaurantId = restaurant.getId();
        String requestBody = objectMapper.writeValueAsString(Collections.singletonMap("seatsNumber", seatsNumber));

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("bad parameter type"));
    }

    @Test
    void Given_InvalidRestuarantId_When_AddingTable_Then_Fails() throws Exception {
        int restaurantId = 5;
        String requestBody = objectMapper.writeValueAsString(Collections.singletonMap("seatsNumber", "4"));

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(null);
        mockMvc.perform(post("/tables/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("restaurant not found"));
    }
}
