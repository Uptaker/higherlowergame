package com.tammeoja.higherlower.services;

import com.tammeoja.higherlower.entities.GameRound;
import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.repositories.GameRoundRepository;
import com.tammeoja.higherlower.repositories.MovieRepository;
import org.junit.jupiter.api.Test;

import static com.tammeoja.higherlower.entities.GameRound.State.*;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO
// button icons (up, down)
// finishedAt, show if homepage if unfinished
// separate highscore for each gamemode + best highschore
// hardmode
// deploy to higherlower.tammeoja.com

class GameRoundServiceTest {
    MovieRepository movieRepository = mock();
    GameRoundRepository gameRoundRepository = mock();
    GameRoundService service = new GameRoundService(gameRoundRepository, movieRepository);

    @Test
    void findLast() {
        var id = randomUUID();
        GameRound gameRound = GameRound.builder().state(WIN).build();
        when(service.findLast(any())).thenReturn(gameRound);

        assertThat(service.findLast(id)).isEqualTo(gameRound);
        verify(gameRoundRepository).findLast(id);
    }

    @Test
    void generate_for_next_round() {
        var gameSessionId = randomUUID();
        var nextMovieId = randomUUID();
        var updatedGameRound = GameRound.builder().id(randomUUID()).nextMovieId(randomUUID()).build();

        when(movieRepository.randomExcludingPlayedMoviesFor(any(), any())).thenReturn(nextMovieId);
        when(gameRoundRepository.findLast(any())).thenReturn(updatedGameRound);

        service.generate(gameSessionId);

        verify(gameRoundRepository).findLast(gameSessionId);
        verify(movieRepository).randomExcludingPlayedMoviesFor(gameSessionId, updatedGameRound.nextMovieId());
        verify(gameRoundRepository).create(gameSessionId, updatedGameRound.nextMovieId(), nextMovieId);
    }

    @Test
    void generate_initial_round() {
        var gameSessionId = randomUUID();
        var currentMovieId = randomUUID();
        var nextMovieId = randomUUID();

        when(movieRepository.random()).thenReturn(currentMovieId);
        when(movieRepository.randomExcludingMovie(any())).thenReturn(nextMovieId);
        when(gameRoundRepository.findLast(any())).thenReturn(null);

        service.generate(gameSessionId);

        verify(gameRoundRepository).findLast(gameSessionId);
        verify(movieRepository).random();
        verify(movieRepository).randomExcludingMovie(currentMovieId);
        verify(gameRoundRepository).create(gameSessionId, currentMovieId, nextMovieId);
    }

    @Test
    void setState_WIN() {
        var gameRoundView = GameRoundView.builder().gameSessionId(randomUUID()).id(randomUUID()).build();
        var nextMovieId = randomUUID();
        var updatedGameRound = GameRound.builder().id(gameRoundView.id()).nextMovieId(randomUUID()).build();

        when(movieRepository.randomExcludingPlayedMoviesFor(any(), any())).thenReturn(nextMovieId);
        when(gameRoundRepository.findLast(any())).thenReturn(updatedGameRound);

        service.setState(WIN, gameRoundView);

        verify(gameRoundRepository).setState(gameRoundView.id(), WIN);
        verify(gameRoundRepository).findLast(gameRoundView.gameSessionId());
        verify(movieRepository).randomExcludingPlayedMoviesFor(gameRoundView.gameSessionId(), updatedGameRound.nextMovieId());
        verify(gameRoundRepository).create(gameRoundView.gameSessionId(), updatedGameRound.nextMovieId(), nextMovieId);
    }

    @Test
    void setState_FAIL() {
        var gameRoundView = GameRoundView.builder().gameSessionId(randomUUID()).id(randomUUID()).build();

        service.setState(FAIL, gameRoundView);

        verify(gameRoundRepository).setState(gameRoundView.id(), FAIL);
        verifyNoMoreInteractions(movieRepository, gameRoundRepository);
    }

    @Test
    void setState_PENDING() {
        var gameRoundView = GameRoundView.builder().gameSessionId(randomUUID()).id(randomUUID()).build();

        assertThrows(IllegalStateException.class, () -> service.setState(PENDING, gameRoundView));
    }
}
