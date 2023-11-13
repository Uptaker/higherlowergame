package com.tammeoja.higherlower.services;

import com.tammeoja.higherlower.entities.GameRound;
import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.repositories.GameRoundRepository;
import com.tammeoja.higherlower.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoundService {
    private final GameRoundRepository gameRoundRepository;
    private final MovieRepository movieRepository;

    public GameRound findLast(UUID gameSessionId) {
        return gameRoundRepository.findLast(gameSessionId);
    }

    public void generate(UUID gameSessionId) {
        var round = gameRoundRepository.findLast(gameSessionId);
        if (round == null) generateFirstRound(gameSessionId);
        else generateNext(gameSessionId, round);
    }

    private void generateFirstRound(UUID gameSessionId) {
        var currentMovieId = movieRepository.random();
        var nextMovieId = movieRepository.randomExcludingMovie(currentMovieId);
        gameRoundRepository.create(gameSessionId, currentMovieId, nextMovieId);
    }

    public void setState(GameRound.State state, GameRoundView gameRoundView) {
        switch (state) {
            case WIN -> {
                gameRoundRepository.setState(gameRoundView.id(), state);
                generate(gameRoundView.gameSessionId());
            }
            case FAIL -> gameRoundRepository.setState(gameRoundView.id(), state);
            case PENDING -> throw new IllegalStateException("Not possible to set game round as PENDING");
        }
    }

    private void generateNext(UUID gameSessionId, GameRound gameRound) {
        gameRoundRepository.create(gameSessionId, gameRound.nextMovieId(), movieRepository.randomExcludingPlayedMoviesFor(gameSessionId, gameRound.nextMovieId()));   }

    public List<GameRound> rounds(UUID gameSessionId) {
        return gameRoundRepository.rounds(gameSessionId);
    }
}
