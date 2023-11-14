package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.entities.GameSession.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.tammeoja.higherlower.entities.GameRound.State.WIN;

@Repository
@RequiredArgsConstructor
public class GameSessionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UUID create(UUID userId, Category category) {
        UUID gameId = UUID.randomUUID();
        jdbcTemplate.update("insert into game_sessions (id, userId, category) values (:id, :userId, :category)",
          Map.of("id", gameId, "userId", userId, "category", category.name()));
        return gameId;
    }

    public GameSession find(UUID gameId) {
        try {
            return jdbcTemplate.queryForObject("""
                select *, (select count(id) from game_rounds where gameSessionId = :id and state = :winState) as score
                from game_sessions where id = :id
                """, Map.of("id", gameId, "winState", WIN.name()),
                DataClassRowMapper.newInstance(GameSession.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer currentScore(UUID gameId) {
        try {
            return jdbcTemplate.queryForObject("""
                select count(id) from game_rounds where gameSessionId = :id and state = :winState
                """, Map.of("id", gameId, "winState", WIN.name()),
                Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public List<GameSession> findByUserId(UUID userId) {
        try {
            return jdbcTemplate.query("""
                select s.*, (select count(id) from game_rounds where gameSessionId = s.id and state = :winState) as score
                from game_sessions s where userId = :userId order by createdAt desc
                """, Map.of("userId", userId, "winState", WIN.name()),
                DataClassRowMapper.newInstance(GameSession.class));
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    public void markAsFinished(UUID gameSessionId) {
        jdbcTemplate.update(" update game_sessions set finishedAt = current_timestamp where id = :id", Map.of("id", gameSessionId));
    }
}

