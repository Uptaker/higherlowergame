package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameSession.Category.*;
import static java.util.UUID.randomUUID;

@Repository
@RequiredArgsConstructor
public class MovieRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public final RowMapper<Movie> MOVIE_MAPPER = (rs, rowCount) -> Movie.builder()
      .id(UUID.fromString(rs.getString("id")))
      .runtime(rs.getDouble("runtime"))
      .tagline(rs.getString("tagline"))
      .title(rs.getString("title"))
      .originalTitle(rs.getString("original_title"))
      .voteCount(rs.getInt("vote_count"))
      .releaseDate(rs.getDate("release_date").toLocalDate())
      .revenue(rs.getLong("revenue"))
      .voteAverage(rs.getDouble("vote_average"))
      .overview(rs.getString("overview"))
      .popularity(rs.getBigDecimal("popularity"))
      .build();

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

    public UUID random() {
        return jdbcTemplate.queryForObject("""
            select id from movies order by random() limit 1
            """, Map.of(), UUID.class);
    }

    public List<UUID> findIdsByGameSession(UUID gameSessionId) {
        return jdbcTemplate.queryForList("""
            select distinct m.id from movies m
            left join game_rounds r1 on r1.currentMovieId = m.id
            left join game_rounds r2 on r2.nextMovieId = m.id
            where r1.gameSessionId = :gameSessionId and r2.gameSessionId = :gameSessionId
                        """,
                Map.of("gameSessionId", gameSessionId), UUID.class);
    }

    public UUID randomExcludingPlayedMoviesFor(UUID gameSessionId, UUID currentMovieId) {
        return jdbcTemplate.queryForObject("""
            select id from (with currentMovie as (select * from movies where id = :currentMovieId)
            select m.id, category as category from movies m
            cross join (select category from game_sessions where id = :gameId)
            where id not in (select distinct currentMovieId from game_rounds where gameSessionId = :gameId)
            and id not in (select distinct nextMovieId from game_rounds where gameSessionId = :gameId)
            and (case when category = :popularityCategory then m.popularity != (select popularity from currentMovie) else true end)
            and (case when category = :runtimeCategory then m.runtime != (select runtime from currentMovie) else true end)
            and (case when category = :revenueCategory then m.revenue != (select revenue from currentMovie) else true end)
            and (case when category = :voteAverageCategory then m.vote_average != (select vote_average from currentMovie) else true end)
            order by random() limit 1) randomId
            """, Map.of(
            "gameId", gameSessionId,
            "currentMovieId", currentMovieId,
            "popularityCategory", POPULARITY.name(),
            "runtimeCategory", RUNTIME.name(),
            "revenueCategory", REVENUE.name(),
            "voteAverageCategory", VOTE_AVERAGE.name()
        ), UUID.class);
    }
}