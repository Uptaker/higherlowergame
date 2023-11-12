package com.tammeoja.higherlower.services;

import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.repositories.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;

    public UUID start(UUID userId, GameSession.Category category) {
        return gameSessionRepository.create(userId, category);
    }
}
