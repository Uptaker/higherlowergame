package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.controllers.GameSessionController.GameSessionScores;
import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.services.GameRoundService;
import com.tammeoja.higherlower.utils.BaseRepositoryTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameRound.State.FAIL;
import static com.tammeoja.higherlower.entities.GameRound.State.WIN;
import static com.tammeoja.higherlower.entities.GameSession.Category.*;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class GameSessionRepositoryTest extends BaseRepositoryTest {
    @Resource
    GameSessionRepository repository;

    @Resource
    GameRoundService gameRoundService;

    UUID userId = randomUUID();

    @Test
    void saveAndLoad() {
        var gameId = repository.create(userId, REVENUE, true);

        var result = repository.find(gameId);
        assertThat(result).isEqualTo(
          GameSession.builder().id(gameId).userId(userId).score(0).createdAt(result.createdAt()).finishedAt(null).category(REVENUE).hard(true).build()
        );
    }

    @Test
    void markAsFinished() {
        var gameId = createGame();

        assertThat(repository.find(gameId).finishedAt()).isNull();
        repository.markAsFinished(gameId);
        assertThat(repository.find(gameId).finishedAt()).isNotNull();
    }

    @Test
    void currentScore() {
        var gameId = createGame();
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

    @Test
    void scores() {
        var revenueGameId = createGameWithCategory(REVENUE);
        var revenueGameId2 = createGameWithCategory(REVENUE);
        var popularityGameId = createGameWithCategory(POPULARITY);
        var voteAverageGameId = createGameWithCategory(VOTE_AVERAGE);

        generateAndWinRound(revenueGameId);
        generateAndWinRound(revenueGameId);
        generateAndWinRound(revenueGameId);
        generateAndWinRound(revenueGameId);
        generateAndWinRound(revenueGameId);
        generateAndWinRound(revenueGameId);

        generateAndWinRound(revenueGameId2);
        generateAndWinRound(revenueGameId2);
        generateAndWinRound(revenueGameId2);

        generateAndWinRound(popularityGameId);
        generateAndWinRound(popularityGameId);

        generateAndWinRound(voteAverageGameId);
        generateAndWinRound(voteAverageGameId);
        generateAndWinRound(voteAverageGameId);
        generateAndWinRound(voteAverageGameId);

        assertThat(repository.scores(userId)).isEqualTo(
                new GameSessionScores(6, 2, 4, 0, 6)
        );
    }

    private UUID createGameWithCategory(GameSession.Category category) {
        return repository.create(userId, category, false);
    }

    private void generateAndWinRound(UUID gameId) {
        gameRoundService.generate(gameId);
        var lastRound = gameRoundService.findLast(gameId);
        gameRoundService.setState(WIN, GameRoundView.builder().id(lastRound.id()).gameSessionId(gameId).build());
    }

    private UUID createGame() {
        return createGameWithCategory(REVENUE);
    }
}