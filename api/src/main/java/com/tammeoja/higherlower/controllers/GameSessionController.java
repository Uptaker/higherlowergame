package com.tammeoja.higherlower.controllers;

import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.entities.GameSession.Category;
import com.tammeoja.higherlower.services.GameSessionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game-session")
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @PostMapping("/start/{category}")
    public UUID start(@CookieValue("userId") UUID userId, @PathVariable Category category) {
        return gameSessionService.start(userId, category);
    }
}
