package com.tammeoja.higherlower.entities;

import jdk.jfr.Category;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record GameSession(
   UUID id,
   UUID userId,
   Category category,
   int score,
   Instant finishedAt,
   Instant createdAt
) {
    public enum Category {
        VOTE_AVERAGE, POPULARITY, RUNTIME, REVENUE
    }
}
