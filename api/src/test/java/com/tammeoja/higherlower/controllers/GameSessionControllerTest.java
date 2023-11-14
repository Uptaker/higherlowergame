package com.tammeoja.higherlower.controllers;

import com.tammeoja.higherlower.services.GameSessionService;
import org.junit.jupiter.api.Test;

import static com.tammeoja.higherlower.entities.GameSession.Category.RUNTIME;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameSessionControllerTest {
    GameSessionService gameSessionService = mock();
    GameSessionController controller = new GameSessionController(gameSessionService);

    @Test
    void start() {
        var gameId = randomUUID();
        var userId = randomUUID();
        when(gameSessionService.start(any(), any(), anyBoolean())).thenReturn(gameId);

        assertThat(controller.start(userId, RUNTIME, true)).isEqualTo(gameId);

        verify(gameSessionService).start(userId, RUNTIME, true);
    }
}