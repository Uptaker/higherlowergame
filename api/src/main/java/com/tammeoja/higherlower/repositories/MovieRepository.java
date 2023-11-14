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
        String query = """
        with currentMovie as (select * from movies where id = :currentMovieId),
        gameCategories as (select category from game_sessions where id = :gameId)
        select coalesce(id, (select fb.id from movies fb
            cross join currentMovie cm
            cross join gameCategories gc
            where fb.id != :currentMovieId
            and fb.id not in (select distinct currentMovieId from game_rounds where gameSessionId = :gameId)
            and fb.id not in (select distinct nextMovieId from game_rounds where gameSessionId = :gameId)
            and (
                    (gc.category = :popularity and fb.popularity != cm.popularity and fb.popularity != 0)
                    or (gc.category = :runtime and fb.runtime != cm.runtime and fb.runtime != 0)
                    or (gc.category = :revenue and fb.revenue != cm.revenue and fb.revenue != 0)
                    or (gc.category = :voteAverage and fb.vote_average != cm.vote_average and fb.vote_average != 0)
            )
            order by random() limit 1)) from (
            select m.id from movies m
            cross join currentMovie cm
            cross join gameCategories gc
            where m.id <> cm.id
            and m.id not in (select distinct currentMovieId from game_rounds where gameSessionId = :gameId)
            and m.id not in (select distinct nextMovieId from game_rounds where gameSessionId = :gameId)
            and (
                (exists (select 1 from game_sessions where id = :gameId and hard = true) and (
                    (gc.category = :popularity and abs(m.popularity - cm.popularity) <= 15 and m.popularity != 0)
                    or (gc.category = :runtime and abs(m.runtime - cm.runtime) <= 15.0 and m.runtime != 0)
                    or (gc.category = :revenue and abs(m.revenue - cm.revenue) <= 2000000 and m.revenue != 0)
                    or (gc.category = :voteAverage and abs(m.vote_average - cm.vote_average) <= 0.5 and m.vote_average != 0)
                ))
                or (not exists (select 1 from game_sessions where id = :gameId and hard = true) and (
                    (gc.category = :popularity and m.popularity != cm.popularity and m.popularity != 0)
                    or (gc.category = :runtime and m.runtime != cm.runtime and m.runtime != 0)
                    or (gc.category = :revenue and m.revenue != cm.revenue and m.revenue != 0)
                    or (gc.category = :voteAverage and m.vote_average != cm.vote_average and m.vote_average != 0)
                ))
            )
            order by random() limit 1
        ) randomId
    """;

        return jdbcTemplate.queryForObject(query, Map.of(
                "gameId", gameSessionId,
                "currentMovieId", currentMovieId,
                "popularity", POPULARITY.name(),
                "runtime", RUNTIME.name(),
                "revenue", REVENUE.name(),
                "voteAverage", VOTE_AVERAGE.name()
        ), UUID.class);
    }
}