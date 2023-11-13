package com.tammeoja.higherlower.entities;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record GameRoundView(
    UUID id,
    UUID gameSessionId,
    Movie current,
    Movie next,
    GameRound.State state,
    Instant createdAt
) {
}
