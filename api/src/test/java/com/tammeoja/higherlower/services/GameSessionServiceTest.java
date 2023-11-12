package com.tammeoja.higherlower.services;

import com.tammeoja.higherlower.repositories.GameSessionRepository;
import org.junit.jupiter.api.Test;

import static com.tammeoja.higherlower.entities.GameSession.Category.RUNTIME;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameSessionServiceTest {
    GameSessionRepository gameSessionRepository = mock();
    GameSessionService service = new GameSessionService(gameSessionRepository);

    @Test
    void start() {
        var gameId = randomUUID();
        var userId = randomUUID();
        when(gameSessionRepository.create(any(), any())).thenReturn(gameId);

        assertThat(service.start(userId, RUNTIME)).isEqualTo(gameId);
        verify(gameSessionRepository).create(userId, RUNTIME);
    }
}