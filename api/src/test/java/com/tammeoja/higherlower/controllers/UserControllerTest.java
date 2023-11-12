package com.tammeoja.higherlower.controllers;

import com.tammeoja.higherlower.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.*;

class UserControllerTest {

    UserService userService = mock();
    UserController controller = new UserController(userService);
    HttpServletResponse response = mock();

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userService);
    }

    @Test
    void me_no_existing_user() {
        controller.me(null, response);
        verify(userService).setAsUser(response);
    }

    @Test
    void me_user_already_exists() {
        controller.me(randomUUID(), response);
        verifyNoInteractions(userService);
    }
}