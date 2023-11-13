package com.tammeoja.higherlower.controllers;

import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.entities.GameSession.Category;
import com.tammeoja.higherlower.services.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game-sessions")
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @GetMapping
    public List<GameSession> list(@CookieValue("userId") UUID userId) {
        return gameSessionService.findByUserId(userId);
    }

    @GetMapping("/{gameSessionId}")
    public GameSessionView rounds(@CookieValue("userId") UUID userId, @PathVariable UUID gameSessionId) {
        return gameSessionService.view(gameSessionId, userId);
    }

    @PostMapping("/start/{category}")
    public UUID start(@CookieValue("userId") UUID userId, @PathVariable Category category) {
        return gameSessionService.start(userId, category);
    }

    @GetMapping("/{gameSessionId}/round")
    public GameRoundView findRound(@PathVariable UUID gameSessionId) {
        return gameSessionService.findRound(gameSessionId);
    }

    @PostMapping("/{gameSessionId}/guess")
    public Map<String, Boolean> guess(@PathVariable UUID gameSessionId, @RequestBody GuessRequest guessRequest) {
        return Map.of("correct", gameSessionService.guess(gameSessionId, guessRequest.isHigher));
    }

    public record GuessRequest(boolean isHigher) {}
    public record GameSessionView(GameSession session, List<GameRoundView> rounds) {}
}
