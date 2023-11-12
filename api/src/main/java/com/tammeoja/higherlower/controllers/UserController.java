package com.tammeoja.higherlower.controllers;

import com.tammeoja.higherlower.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @ResponseStatus(NO_CONTENT)
    void me(@CookieValue(value = "userId", required = false) UUID userId, HttpServletResponse response) {
        if (userId == null) userService.setAsUser(response);
    }
}
