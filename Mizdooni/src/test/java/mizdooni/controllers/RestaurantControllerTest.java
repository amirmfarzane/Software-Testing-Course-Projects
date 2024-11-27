package mizdooni.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
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

@AutoConfigureMockMvc
@SpringBootTest(classes = MizdooniApplication.class)
public class RestaurantControllerTest {

    @MockBean
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    @Autowired
    private MockMvc mockMvc;

    Restaurant restaurant1, restaurant2, restaurant3;
    Address address;
    User manager1, manager2, manager3;

    @BeforeEach
    public void setup() {
        manager1 = new User("test1", "test123", "test1@gmail.com", null, User.Role.manager);
        manager2 = new User("test2", "test123", "test2@gmail.com", null, User.Role.manager);
        address = new Address("Enghelab Square", "Tehran", "12345");
        restaurant1 = new Restaurant("Kababi", manager2, "Iranian", LocalTime.of(0, 0),
            LocalTime.of(23, 59), "Tehran", null , "image");
        restaurant2 = new Restaurant("Choloee", manager1, "Chiness", LocalTime.of(12, 0),
            LocalTime.of(21, 59), "Tehran", null , "image");
        restaurant3 = new Restaurant("Dizii", manager1, "Iranian", LocalTime.of(4, 0),
            LocalTime.of(22, 59), "Qom", null , "image");
        Table table1 = new Table(1, 1, 4);
        Table table2 = new Table(2, 1, 3);

        restaurant1.addTable(table1);
        restaurant2.addTable(table2);
    }


    @Test
    public void Given_ValidRestaurantId_When_GetRestaurant_Then_Return() throws Exception{

        int restaurantId = restaurant1.getId();
        String baseUrl = "/restaurants";

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant1);

        mockMvc.perform(get(baseUrl+"/{restaurantId}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("restaurant found"))
            .andExpect(jsonPath("$.data.id").value(restaurantId));
    }

    @Test
    public void Given_InvalidRestaurantId_When_RequestingRestaurant_Then_ThrowBadRequest() throws Exception{
        int restaurantId = 99;
        String baseUrl = "/restaurants"+restaurantId;

        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant1);

        mockMvc.perform(get(baseUrl+"/{restaurantId}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void Given_RuntimeError_When_RequestingRestaurants_Then_ThrowBadRequest() throws Exception{
        String baseUrl = "/restaurants";

        when(restaurantService.getRestaurants(anyInt(), any())).thenThrow(new RuntimeException("Runtime error"));

        mockMvc.perform(get(baseUrl)
                .param("page", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Runtime error"));
    }


    @Test
    public void Given_ValidManagerId_When_RequestingRestaurant_Then_ThrowBadRequest() throws Exception{
        int mangaerId = manager1.getId();
        String baseUrl = "/restaurants/manager";
        List<Restaurant> mockRestaurants = List.of(restaurant2, restaurant3);
        when(restaurantService.getManagerRestaurants(mangaerId)).thenReturn(mockRestaurants);
        mockMvc.perform(get(baseUrl+"/{restaurantId}", mangaerId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("manager restaurants listed"))
            .andExpect(jsonPath("$.data.length()").value(mockRestaurants.size()))
            .andExpect(jsonPath("$.data[0].name").value(restaurant2.getName()))
            .andExpect(jsonPath("$.data[1].name").value(restaurant3.getName()));
    }

    @Test
    public void Given_RuntimeError_When_GetManagerProcess_Then_ThrowBadRequest() throws Exception{
        String baseUrl = "/restaurants/manager";

        when(restaurantService.getManagerRestaurants(anyInt())).thenThrow(new RuntimeException("Runtime error"));

        mockMvc.perform(get(baseUrl+"/{manager-id}", "2"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Runtime error"));
    }
    

    @Test
    public void Given_ValidParameters_When_AddRestaurants_Then_Add() throws Exception{
        String baseUrl = "/restaurants";
        Map<String, Object> params = Map.of(
            "name", "FeriKesafat",
            "type", "Iranian",
            "startTime", "20:00",
            "endTime", "24:00",
            "description", "Fast food",
            "image", "Ferri.png",
            "address", Map.of(
                "country", "Iran",
                "city", "Tehran",
                "street", "Ferri St."
            )
        );
        int mockRestId = 1;
        when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class), anyString(), any(
            Address.class), anyString()))
            .thenReturn(mockRestId);
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(params)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("restaurant added"))
            .andExpect(jsonPath("$.data").value(mockRestId));
    }

    @Test
    public void Given_MissTypeParameter_When_AddRestaurants_Then_BadRequest() throws Exception{
        String baseUrl = "/restaurants";
        Map<String, Object> params = Map.of(
            "name", "FeriKesafat",
            "startTime", "20:00",
            "endTime", "24:00",
            "description", "Fast food",
            "image", "Ferri.png",
            "address", Map.of(
                "country", "Iran",
                "city", "Tehran",
                "street", "Ferri St."
            )
        );
        int mockRestId = 1;
        when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class), anyString(), any(
            Address.class), anyString()))
            .thenReturn(mockRestId);
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(params)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value( "parameters missing"));
    }

    @Test
    public void Given_IncorrectLocalDateParameter_When_AddRestaurants_Then_BadRequest() throws Exception{
        String baseUrl = "/restaurants";
        Map<String, Object> params = Map.of(
            "name", "FeriKesafat",
            "type", "Iranian",
            "startTime", "mew mew",
            "endTime", "22:00",
            "description", "Fast food",
            "image", "Ferri.png",
            "address", Map.of(
                "country", "Iran",
                "city", "Tehran",
                "street", "Ferri St."
            )
        );
        int mockRestId = 1;
        when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class), anyString(), any(
            Address.class), anyString()))
            .thenReturn(mockRestId);
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(params)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value( "bad parameter type"));
    }

    @Test
    public void Given_BlankName_When_AddRestaurants_Then_BadRequest() throws Exception{
        String baseUrl = "/restaurants";
        Map<String, Object> params = Map.of(
            "name", "",
            "startTime", "20:00",
            "endTime", "24:00",
            "type", "Iranian",
            "description", "Fast food",
            "image", "Ferri.png",
            "address", Map.of(
                "country", "Iran",
                "city", "Tehran",
                "street", "Ferri St."
            )
        );
        int mockRestId = 1;
        when(restaurantService.addRestaurant(anyString(), anyString(), any(LocalTime.class), any(LocalTime.class), anyString(), any(
            Address.class), anyString()))
            .thenReturn(mockRestId);
        mockMvc.perform(post(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(params)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value( "parameters missing"));
    }

    @Test
    public void Given_TakenaName_When_ValidatingRestaurantName_Then_ReturnConflict() throws Exception{
        String baseUrl = "/validate/restaurant-name";
        String restName = "mew";
        when(restaurantService.restaurantExists(restName)).thenReturn(true);
        mockMvc.perform(get(baseUrl)
                .param("data", restName)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("restaurant name is taken"));
    }

    @Test
    public void Given_AvailableName_When_ValidatingRestaurantName_Then_ReturnAvailable() throws Exception{
        String baseUrl = "/validate/restaurant-name";
        String restName = "mew";
        when(restaurantService.restaurantExists(restName)).thenReturn(false);
        mockMvc.perform(get(baseUrl)
                .param("data", restName)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("restaurant name is available"));
    }

    @Test
    public void Given_Request_When_GettingRestaurantTypes_Then_ReturnTypesList() throws Exception{
        String baseUrl = "/restaurants/types";
        Set types = Set.of("a","b");
        when(restaurantService.getRestaurantTypes()).thenReturn(types);
        mockMvc.perform(get(baseUrl)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("restaurant types"));
    }

    @Test
    public void Given_Error_When_GettingRestaurantTypes_Then_ReturnException() throws Exception{
        String baseUrl = "/restaurants/types";
        Set types = Set.of("a","b");
        when(restaurantService.getRestaurantTypes()).thenThrow(new RuntimeException("Runtime error"));
        mockMvc.perform(get(baseUrl)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Runtime error"));
    }

    @Test
    public void Given_Request_When_GettingRestaurantLocations_Then_ReturnLocationsMap() throws Exception{
        String baseUrl = "/restaurants/locations";
        Map<String, Set<String>> locations = Map.of(
            "Iran", Set.of("Qom", "Tehran")
        );
        when(restaurantService.getRestaurantLocations()).thenReturn(locations);
        mockMvc.perform(get(baseUrl)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("restaurant locations"))
            .andExpect(jsonPath("$.data.Iran").isArray());
    }

}