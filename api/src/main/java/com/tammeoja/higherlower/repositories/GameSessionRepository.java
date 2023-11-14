package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.controllers.GameSessionController.GameSessionScores;
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
import static com.tammeoja.higherlower.entities.GameSession.Category.*;

@Repository
@RequiredArgsConstructor
public class GameSessionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UUID create(UUID userId, Category category, boolean hardMode) {
        UUID gameId = UUID.randomUUID();
        jdbcTemplate.update("insert into game_sessions (id, userId, category, hard) values (:id, :userId, :category, :hard)",
          Map.of("id", gameId, "userId", userId, "category", category.name(), "hard", hardMode));
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

    public GameSessionScores scores(UUID userId) {
        try {
            return jdbcTemplate.queryForObject("""
                WITH round_scores AS (
                    SELECT COUNT(r.id) OVER(PARTITION BY s.Id) AS score_per_session, s.category
                    FROM game_sessions s JOIN game_rounds r ON r.gameSessionId = s.Id
                    WHERE s.userId = :userId AND r.state = :winState)
                SELECT
                    COALESCE(MAX(s.score_per_session), 0) AS highScore,
                    COALESCE(MAX(CASE WHEN s.category = :popularity THEN s.score_per_session ELSE 0 END), 0) AS popularityScore,
                    COALESCE(MAX(CASE WHEN s.category = :voteAverage THEN s.score_per_session ELSE 0 END), 0) AS voteAverageScore,
                    COALESCE(MAX(CASE WHEN s.category = :revenue THEN s.score_per_session ELSE 0 END), 0) AS revenueScore,
                    COALESCE(MAX(CASE WHEN s.category = :runtime THEN s.score_per_session ELSE 0 END), 0) AS runtimeScore
                FROM round_scores s;
            """, Map.of(
            "userId", userId,
                "winState", WIN.name(),
                "popularity", POPULARITY.name(),
                "voteAverage", VOTE_AVERAGE.name(),
                "revenue", REVENUE.name(),
                "runtime", RUNTIME.name()
                ), DataClassRowMapper.newInstance(GameSessionScores.class));
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

