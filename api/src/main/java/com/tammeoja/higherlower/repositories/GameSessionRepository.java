package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.entities.GameSession.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

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
            return jdbcTemplate.queryForObject("select * from game_sessions where id = :id", Map.of("id", gameId),
                    DataClassRowMapper.newInstance(GameSession.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}

