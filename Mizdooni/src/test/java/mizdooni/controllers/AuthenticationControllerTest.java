package mizdooni.controllers;

import static mizdooni.controllers.ControllerUtils.PARAMS_MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.model.User.Role;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    User user;
    private Address address;
    private String username;
    private String password;
    private String email;

    @Mock
    UserService userService;

    @InjectMocks
    AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        address = new Address("Enghelab Square", "Tehran", "12345");
        user = new User(username, password, email, address, Role.client);
    }

    @Test
    @DisplayName("Test: Successful retrieval of current user")
    public void testUser_Success() {
        when(userService.getCurrentUser()).thenReturn(user);
        Response response = authenticationController.user();
        assertEquals("current user", response.getMessage());
        assertEquals(user, response.getData());
    }

    @Test
    @DisplayName("Test: Attempt to get current user without login")
    public void testUser_NoUserLoggedIn() {
        when(userService.getCurrentUser()).thenReturn(null);
        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.user();
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Successful logout")
    public void testLogout_Success() throws Exception {
        when(userService.logout()).thenReturn(true);
        Response logoutResponse = authenticationController.logout();
        assertEquals("logout successful", logoutResponse.getMessage());
    }

    @Test
    @DisplayName("Test: Successful login with valid credentials")
    public void testLogin_Success() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "user1");
        params.put("password", "password");

        when(userService.login("user1", "password")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);

        Response response = authenticationController.login(params);
        assertEquals("login successful", response.getMessage());
    }

    @Test
    @DisplayName("Test: Login attempt with missing parameters")
    public void testLogin_MissingParams() {
        Map<String, String> params = new HashMap<>();

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.login(params);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test: Login attempt with invalid credentials")
    public void testLogin_InvalidCredentials() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "user1");
        params.put("password", "wrongPassword");

        when(userService.login("user1", "wrongPassword")).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.login(params);
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Successful signup with valid parameters")
    public void testSignup_Success() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "user1");
        params.put("password", "password");
        params.put("email", "user1@example.com");
        Map<String, String> address = new HashMap<>();
        address.put("country", "Country");
        address.put("city", "City");
        params.put("address", address);
        params.put("role", "client");

        when(userService.login("user1", "password")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);

        Response response = authenticationController.signup(params);
        assertEquals("signup successful", response.getMessage());
    }

    @Test
    @DisplayName("Test: Signup attempt with missing parameters")
    public void testSignup_MissingParams() {
        Map<String, Object> params = new HashMap<>();

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.signup(params));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test: Logout attempt with no user logged in")
    void testLogout_NoUserLoggedIn() {
        when(userService.logout()).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.logout());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Validate username - valid format and availability")
    void testValidateUsername_ValidAndAvailable() {
        String username = "validUsername";
        when(userService.usernameExists(username)).thenReturn(false);

        Response response = authenticationController.validateUsername(username);
        assertEquals("username is available", response.getMessage());
    }

    @Test
    @DisplayName("Test: Validate username - invalid format")
    void testValidateUsername_InvalidFormat() {
        String username = "invalid!Username";

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateUsername(username));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid username format", exception.getMessage());
    }

    @Test
    @DisplayName("Test: Validate email - valid format and not registered")
    void testValidateEmail_ValidAndNotRegistered() {
        String email = "user1@example.com";
        when(userService.emailExists(email)).thenReturn(false);

        Response response = authenticationController.validateEmail(email);
        assertEquals("email not registered", response.getMessage());
    }

    @Test
    @DisplayName("Test: Validate email - invalid format")
    void testValidateEmail_InvalidFormat() {
        String email = "invalid-email";

        ResponseException exception = assertThrows(ResponseException.class, () -> authenticationController.validateEmail(email));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid email format", exception.getMessage());
    }
}
