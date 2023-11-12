package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Repository
@RequiredArgsConstructor
public class MovieRepository {
    public final RowMapper<Movie> MOVIE_MAPPER = (rs, rowCount) -> Movie.builder()
      .id(UUID.fromString(rs.getString("id")))
      .runtime(rs.getDouble("runtime"))
      .tagline(rs.getString("tagline"))
      .title(rs.getString("title"))
      .originalTitle(rs.getString("original_title"))
      .voteCount(rs.getInt("vote_count"))
      .releaseDate(rs.getDate("release_date").toLocalDate())
      .revenue(rs.getInt("revenue"))
      .voteAverage(rs.getDouble("vote_average"))
      .overview(rs.getString("overview"))
      .popularity(rs.getBigDecimal("popularity"))
      .build();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UUID save(Movie movie) {
        var movieId = movie.id() == null ? randomUUID() : movie.id();
        var params = new MapSqlParameterSource();
        params.addValue("id", movieId);
        params.addValue("runtime", movie.runtime());
        params.addValue("tagline", movie.tagline());
        params.addValue("title", movie.title());
        params.addValue("original_title", movie.originalTitle());
        params.addValue("vote_count", movie.voteCount());
        params.addValue("release_date", movie.releaseDate());
        params.addValue("revenue", movie.revenue());
        params.addValue("vote_average", movie.voteAverage());
        params.addValue("overview", movie.overview());
        params.addValue("popularity", movie.popularity());

        jdbcTemplate.update("""
        insert into movies (id, runtime, tagline, title, original_title, vote_count, release_date, revenue, vote_average, overview, popularity)
        values (:id, :runtime, :tagline, :title, :original_title, :vote_count, :release_date, :revenue, :vote_average, :overview, :popularity)
        """, params);
        return movieId;
    }

    public Movie find(UUID id) {
        try {
            return jdbcTemplate.queryForObject("select * from movies where id = :id", Map.of("id", id), MOVIE_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}