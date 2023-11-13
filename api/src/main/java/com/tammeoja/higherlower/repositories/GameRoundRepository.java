package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.GameRound;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GameRoundRepository {
    private final Clock clock;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public final RowMapper<GameRound> GAME_ROUND_MAPPER = (rs, rowCount) -> GameRound.builder()
            .id(UUID.fromString(rs.getString("id")))
            .gameSessionId(UUID.fromString(rs.getString("gameSessionId")))
            .currentMovieId(UUID.fromString(rs.getString("currentMovieId")))
            .nextMovieId(UUID.fromString(rs.getString("nextMovieId")))
            .state(Enum.valueOf(GameRound.State.class, rs.getString("state")))
            .createdAt(rs.getTimestamp("createdAt").toInstant())
            .build();

    public GameRound create(UUID gameSessionId, UUID currentMovieId, UUID nextMovieId) {
        return jdbcTemplate.queryForObject("""
          insert into game_rounds (gameSessionId, currentMovieId, nextMovieId, createdAt) values (:gameSessionId, :currentMovieId, :nextMovieId, :createdAt)
          returning *
           """,
          Map.of("gameSessionId", gameSessionId, "currentMovieId", currentMovieId, "nextMovieId", nextMovieId, "createdAt", Timestamp.from(Instant.now(clock))),
                GAME_ROUND_MAPPER);
    }

    public GameRound findLast(UUID gameSessionId) {
        try {
            return jdbcTemplate.queryForObject("""
                  select * from game_rounds where gameSessionId = :gameSessionId
                  order by createdAt desc limit 1
                  """,
                    Map.of("gameSessionId", gameSessionId), GAME_ROUND_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void setState(UUID roundId, GameRound.State state) {
        jdbcTemplate.update("update game_rounds set state = :state where id = :id", Map.of("id", roundId, "state", state.name()));
    }
}

