package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.services.GameRoundService;
import com.tammeoja.higherlower.utils.BaseRepositoryTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import static com.tammeoja.higherlower.entities.GameRound.State.FAIL;
import static com.tammeoja.higherlower.entities.GameRound.State.WIN;
import static com.tammeoja.higherlower.entities.GameSession.Category.REVENUE;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class GameSessionRepositoryTest extends BaseRepositoryTest {
    @Resource
    GameSessionRepository repository;

    @Resource
    GameRoundService gameRoundService;

    @Test
    void saveAndLoad() {
        var userId = randomUUID();
        var gameId = repository.create(userId, REVENUE);

        var result = repository.find(gameId);
        assertThat(result).isEqualTo(
          GameSession.builder().id(gameId).userId(userId).score(0).createdAt(result.createdAt()).finishedAt(null).category(REVENUE).build()
        );
    }

    @Test
    void currentScore() {
        var userId = randomUUID();
        var gameId = repository.create(userId, REVENUE);
        gameRoundService.generate(gameId);
        assertThat(repository.currentScore(gameId)).isEqualTo(0);

        var lastRound = gameRoundService.findLast(gameId);
        gameRoundService.setState(WIN, GameRoundView.builder().id(lastRound.id()).gameSessionId(gameId).build());
        assertThat(repository.currentScore(gameId)).isEqualTo(1);

        gameRoundService.generate(gameId);
        assertThat(repository.currentScore(gameId)).isEqualTo(1);
        lastRound = gameRoundService.findLast(gameId);
        gameRoundService.setState(WIN, GameRoundView.builder().id(lastRound.id()).gameSessionId(gameId).build());
        assertThat(repository.currentScore(gameId)).isEqualTo(2);

        gameRoundService.generate(gameId);
        lastRound = gameRoundService.findLast(gameId);
        gameRoundService.setState(FAIL, GameRoundView.builder().id(lastRound.id()).gameSessionId(gameId).build());
        assertThat(repository.currentScore(gameId)).isEqualTo(2);
    }
}