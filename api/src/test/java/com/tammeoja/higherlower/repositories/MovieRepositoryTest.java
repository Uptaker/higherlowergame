package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.services.GameRoundService;
import com.tammeoja.higherlower.services.GameSessionService;
import com.tammeoja.higherlower.utils.BaseRepositoryTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameSession.Category.REVENUE;
import static com.tammeoja.higherlower.utils.TestData.testMovie;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class MovieRepositoryTest extends BaseRepositoryTest {

    @Resource
    MovieRepository repository;

    @Resource
    GameSessionService gameSessionService;

    @Resource
    GameRoundService gameRoundService;

    @Test
    void save_and_load() {
        var id = repository.save(testMovie);

        assertThat(repository.find(id)).isEqualTo(testMovie.withId(id));
    }

    @Test
    void random() {
        var randomMovie = repository.random();
        assertThat(randomMovie).isNotNull();
    }


    @Test
    void findMoviesByGameSession() {
        generateAndAssertHasMovies();
    }

    @Test
    void randomExcludingPlayedMoviesFor() {
        var gameSessionId = generateAndAssertHasMovies();
        var lastRound = gameRoundService.findLast(gameSessionId);

        var pickedMovies = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            var pickedMovieId = repository.randomExcludingPlayedMoviesFor(gameSessionId, lastRound.nextMovieId());
            assertThat(repository.find(pickedMovieId).revenue()).isNotEqualTo(repository.find(lastRound.nextMovieId()).revenue());
            pickedMovies.add(pickedMovieId);
        }

        assertThat(pickedMovies).doesNotContain(repository.findIdsByGameSession(gameSessionId));
    }

    private UUID generateAndAssertHasMovies(boolean hard) {
        var gameSessionId = gameSessionService.start(randomUUID(), REVENUE, hard);

        for (int i = 0; i < 20; i++) {
            gameRoundService.generate(gameSessionId);
        }

        var movieIds = repository.findIdsByGameSession(gameSessionId);
        assertThat(movieIds).hasSize(20);
        assertThat(movieIds.get(0)).isNotEqualTo(movieIds.get(10));
        return gameSessionId;
    }

    private UUID generateAndAssertHasMovies() {
        return generateAndAssertHasMovies(false);
    }
}