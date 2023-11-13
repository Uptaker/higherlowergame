package com.tammeoja.higherlower.entities;

import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record Movie(
        @With UUID id,
        String originalTitle,
        String overview,
        BigDecimal popularity,
        LocalDate releaseDate,
        long revenue,
        Double runtime,
        String tagline,
        String title,
        Double voteAverage,
        int voteCount
) {
}
