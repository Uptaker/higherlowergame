package com.tammeoja.higherlower.services;

import com.tammeoja.higherlower.entities.GameRound;
import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.entities.Movie;
import com.tammeoja.higherlower.repositories.GameSessionRepository;
import com.tammeoja.higherlower.repositories.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameRound.State.*;
import static com.tammeoja.higherlower.entities.GameSession.Category.RUNTIME;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameSessionServiceTest {
    GameSessionRepository gameSessionRepository = mock();
    MovieRepository movieRepository = mock();
    GameRoundService gameRoundService = mock();
    GameSessionService service = new GameSessionService(gameSessionRepository, movieRepository, gameRoundService);

    UUID gameId = randomUUID();

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(gameSessionRepository, movieRepository, gameRoundService);
    }

    @Test
    void start() {
        var userId = randomUUID();
        when(gameSessionRepository.create(any(), any())).thenReturn(gameId);

        assertThat(service.start(userId, RUNTIME)).isEqualTo(gameId);
        verify(gameSessionRepository).create(userId, RUNTIME);
        verify(gameRoundService).generate(gameId);
    }

    @Test
    void findRound() {
        var gameRound = GameRound.builder().id(randomUUID()).gameSessionId(gameId).currentMovieId(randomUUID()).state(PENDING).nextMovieId(randomUUID()).build();
        var currentMovie = Movie.builder().id(gameRound.currentMovieId()).build();
        var nextMovie = Movie.builder().id(gameRound.nextMovieId()).build();

        when(gameRoundService.findLast(any())).thenReturn(gameRound);
        when(movieRepository.find(any())).thenReturn(currentMovie).thenReturn(nextMovie);
        when(gameSessionRepository.currentScore(any())).thenReturn(1);

        assertThat(service.findRound(gameId)).isEqualTo(
                GameRoundView.builder().id(gameRound.id()).score(1).gameSessionId(gameId).current(currentMovie).next(nextMovie).state(PENDING).build()
        );

        verify(movieRepository).find(gameRound.currentMovieId());
        verify(movieRepository).find(gameRound.nextMovieId());
        verify(gameSessionRepository).currentScore(gameId);
        verify(gameRoundService).findLast(gameId);
    }

    @ParameterizedTest
    @EnumSource(GameSession.Category.class)
    void guess_higher_correct(GameSession.Category category) {
        var currentMovie = Movie.builder().revenue(100).runtime(100.5).voteAverage(100.5).popularity(new BigDecimal("100.999")).build();
        var nextMovie = Movie.builder().revenue(200).runtime(200.5).voteAverage(200.5).popularity(new BigDecimal("200.999")).build();
        assertThatMovieCalculationsAre(true, currentMovie, nextMovie, category, true);
    }

    @ParameterizedTest
    @EnumSource(GameSession.Category.class)
    void guess_higher_incorrect(GameSession.Category category) {
        var currentMovie = Movie.builder().revenue(200).runtime(200.5).voteAverage(200.5).popularity(new BigDecimal("200.999")).build();
        var nextMovie = Movie.builder().revenue(100).runtime(100.5).voteAverage(100.5).popularity(new BigDecimal("100.999")).build();
        assertThatMovieCalculationsAre(false, currentMovie, nextMovie, category, true);
    }

    @ParameterizedTest
    @EnumSource(GameSession.Category.class)
    void guess_lower_correct(GameSession.Category category) {
        var currentMovie = Movie.builder().revenue(200).runtime(200.5).voteAverage(200.5).popularity(new BigDecimal("200.999")).build();
        var nextMovie = Movie.builder().revenue(100).runtime(100.5).voteAverage(100.5).popularity(new BigDecimal("100.999")).build();
        assertThatMovieCalculationsAre(true, currentMovie, nextMovie, category, false);
    }

    @ParameterizedTest
    @EnumSource(GameSession.Category.class)
    void guess_lower_incorrect(GameSession.Category category) {
        var currentMovie = Movie.builder().revenue(100).runtime(100.5).voteAverage(100.5).popularity(new BigDecimal("100.999")).build();
        var nextMovie = Movie.builder().revenue(200).runtime(200.5).voteAverage(200.5).popularity(new BigDecimal("200.999")).build();
        assertThatMovieCalculationsAre(false, currentMovie, nextMovie, category, false);
    }

    private void assertThatMovieCalculationsAre(boolean expectation, Movie currentMovie, Movie nextMovie, GameSession.Category category, boolean isHigher) {
        var gameSession = GameSession.builder().id(gameId).category(category).build();
        var gameRound = GameRound.builder().gameSessionId(gameId).id(randomUUID()).currentMovieId(randomUUID()).nextMovieId(randomUUID()).build();

        when(gameSessionRepository.find(any())).thenReturn(gameSession);
        when(gameRoundService.findLast(any())).thenReturn(gameRound);
        when(movieRepository.find(any())).thenReturn(currentMovie).thenReturn(nextMovie);
        when(gameSessionRepository.currentScore(any())).thenReturn(1);

        assertThat(service.guess(gameId, isHigher)).isEqualTo(expectation);

        verify(gameSessionRepository).find(gameId);
        verify(gameRoundService).findLast(gameId);
        verify(gameRoundService).setState(expectation ? WIN : FAIL, gameRound.toViewBuilder().current(currentMovie).next(nextMovie).score(1).build());
        verify(movieRepository).find(gameRound.currentMovieId());
        verify(movieRepository).find(gameRound.nextMovieId());
        verify(gameSessionRepository).currentScore(gameId);
    }
}