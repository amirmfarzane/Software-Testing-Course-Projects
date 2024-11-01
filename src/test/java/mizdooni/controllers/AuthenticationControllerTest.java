package mizdooni.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.service.UserService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {
    @Mock
    UserService userService;

    @InjectMocks
    AuthenticationControllerTest authenticationController;


    @BeforeEach
    public void setup(){
        when(userService.getCurrentUser()).thenReturn(mock(User.class));




    }


    @Test
    @DisplayName("")
    public void userExistsTest() throws Exception {
    

    }

    @Test
    @DisplayName("")
    public void logoutSuccessfullyTest() throws Exception {
        when(userService.logout()).thenReturn(true);
        Response logoutResponse = authenticationController.logout();
        assertEquals(logoutResponse.getMessage(), "logout successful");
        
        
    }
}
