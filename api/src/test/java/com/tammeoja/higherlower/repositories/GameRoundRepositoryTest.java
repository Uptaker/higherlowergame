package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.GameRound;
import com.tammeoja.higherlower.utils.BaseRepositoryTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameRound.State.*;
import static com.tammeoja.higherlower.entities.GameSession.Category.POPULARITY;
import static org.assertj.core.api.Assertions.assertThat;

class GameRoundRepositoryTest extends BaseRepositoryTest {

    @Resource
    GameRoundRepository repository;

    @Resource
    GameSessionRepository gameSessionRepository;

    @Resource
    MovieRepository movieRepository;

    UUID gameId;

    @BeforeEach
    void setUp() {
        gameId = gameSessionRepository.create(UUID.randomUUID(), POPULARITY);
    }

    @Test
    void save_and_load() {
        var randomMovie = movieRepository.random();
        var randomMovie2 = movieRepository.randomExcludingMovie(randomMovie);

        repository.create(gameId, randomMovie, randomMovie2);
        var result = repository.findLast(gameId);
        assertThat(result).isEqualTo(
            GameRound.builder()
                .id(result.id())
                .gameSessionId(gameId)
                .createdAt(result.createdAt())
                .currentMovieId(randomMovie)
                .nextMovieId(randomMovie2)
                .state(PENDING)
                .build()
        );
    }


    @Test
    void setState() {
        var randomMovie = movieRepository.random();
        var randomMovie2 = movieRepository.randomExcludingMovie(randomMovie);

        var roundId = repository.create(gameId, randomMovie, randomMovie2);
        assertThat(repository.findLast(gameId).state()).isEqualTo(PENDING);

        repository.setState(roundId.id(), FAIL);
        assertThat(repository.findLast(gameId).state()).isEqualTo(FAIL);

        repository.setState(roundId.id(), WIN);
        assertThat(repository.findLast(gameId).state()).isEqualTo(WIN);
    }
}