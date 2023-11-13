package com.tammeoja.higherlower.entities;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record GameRound(
    UUID id,
    UUID gameSessionId,
    UUID currentMovieId,
    UUID nextMovieId,
    State state,
    Instant createdAt
) {
    public enum State {
        WIN, FAIL, PENDING
    }

    public GameRoundView.GameRoundViewBuilder toViewBuilder() {
        return GameRoundView.builder().id(id).gameSessionId(gameSessionId).state(state).createdAt(createdAt);
    }
}

