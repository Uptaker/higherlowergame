package com.tammeoja.higherlower.utils;

import com.tammeoja.higherlower.entities.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TestData {
    public static Movie testMovie = Movie.builder()
        .originalTitle("The Adventures of Random Movie")
        .overview("A thrilling story of randomness and excitement.")
        .popularity(new BigDecimal("123.456000"))
        .releaseDate(LocalDate.parse("2000-05-21"))
        .revenue(999999)
        .runtime(Double.valueOf("120.0"))
        .tagline("Expect the unexpected.")
        .title("Random Movie")
        .voteAverage(Double.valueOf("111.5"))
        .voteCount(555)
        .build();
}
