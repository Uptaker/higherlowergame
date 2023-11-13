package com.tammeoja.higherlower.controllers;

import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.entities.GameSession.Category;
import com.tammeoja.higherlower.services.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-sessions")
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @PostMapping("/start/{category}")
    public UUID start(@CookieValue("userId") UUID userId, @PathVariable Category category) {
        return gameSessionService.start(userId, category);
    }

    @GetMapping("/{gameSessionId}/round")
    public GameRoundView findRound(@PathVariable UUID gameSessionId) {
        return gameSessionService.findRound(gameSessionId);
    }

    @PostMapping("/{gameSessionId}/guess")
    public boolean guess(@PathVariable UUID gameSessionId, @RequestBody boolean isHigher) {
        return gameSessionService.guess(gameSessionId, isHigher);
    }
}
