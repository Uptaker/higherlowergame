package com.tammeoja.higherlower.services;

import com.tammeoja.higherlower.controllers.GameSessionController;
import com.tammeoja.higherlower.entities.GameRound;
import com.tammeoja.higherlower.entities.GameRoundView;
import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.entities.Movie;
import com.tammeoja.higherlower.repositories.GameSessionRepository;
import com.tammeoja.higherlower.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameRound.State.FAIL;
import static com.tammeoja.higherlower.entities.GameRound.State.WIN;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final MovieRepository movieRepository;
    private final GameRoundService gameRoundService;

    public GameSessionController.GameSessionView view(UUID gameSessionId, UUID userId) {
        var gameSession = gameSessionRepository.find(gameSessionId);
        if (!gameSession.userId().equals(userId)) throw new ResponseStatusException(FORBIDDEN);
        var rounds = gameRoundService.rounds(gameSessionId).stream().map(this::asView).toList();
        return new GameSessionController.GameSessionView(gameSession, rounds);
    }

    public GameSession find(UUID gameSessionId, UUID userId) {
        var gameSession = gameSessionRepository.find(gameSessionId);
        if (!gameSession.userId().equals(userId)) throw new ResponseStatusException(FORBIDDEN);
        return gameSession;
    }

    public UUID start(UUID userId, GameSession.Category category) {
        var gameSessionId = gameSessionRepository.create(userId, category);
        gameRoundService.generate(gameSessionId);
        return gameSessionId;
    }

    public GameRoundView findRound(UUID gameSessionId) {
        return findLastRoundAsView(gameSessionId);
    }

    private GameRoundView asView(GameRound gameRound) {
        return gameRound.toViewBuilder()
            .current(movieRepository.find(gameRound.currentMovieId()))
            .next(movieRepository.find(gameRound.nextMovieId()))
            .score(gameSessionRepository.currentScore(gameRound.gameSessionId()))
            .build();
    }

    public boolean guess(UUID gameSessionId, boolean isHigher) {
        var gameSession = gameSessionRepository.find(gameSessionId);
        var lastRound = findLastRoundAsView(gameSessionId);
        var isCorrect = calculateAnswer(isHigher, lastRound, gameSession.category());
        gameRoundService.setState(isCorrect ? WIN : FAIL, lastRound);
        if (!isCorrect) gameSessionRepository.markAsFinished(gameSessionId);
        return isCorrect;
    }

    private GameRoundView findLastRoundAsView(UUID gameSessionId) {
        return asView(gameRoundService.findLast(gameSessionId));
    }

    private boolean calculateAnswer(boolean isHigher, GameRoundView gameRoundView, GameSession.Category category) {
        Movie current = gameRoundView.current();
        Movie next = gameRoundView.next();
        return switch (category) {
            case VOTE_AVERAGE -> isHigher ? next.voteAverage() > current.voteAverage() : next.voteAverage() < current.voteAverage();
            case POPULARITY -> isHigher ? next.popularity().compareTo(current.popularity()) > 0 : next.popularity().compareTo(current.popularity()) < 0;
            case RUNTIME -> isHigher ? next.runtime() > current.runtime() : next.runtime() < current.runtime();
            case REVENUE -> isHigher ? next.revenue() > current.revenue() : next.revenue() < current.revenue();
        };
    }

    public List<GameSession> findByUserId(UUID userId) {
        return gameSessionRepository.findByUserId(userId);
    }
}
