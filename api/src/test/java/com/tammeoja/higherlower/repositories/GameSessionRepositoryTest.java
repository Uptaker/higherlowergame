package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.entities.GameSession;
import com.tammeoja.higherlower.utils.BaseRepositoryTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import static com.tammeoja.higherlower.entities.GameSession.Category.REVENUE;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

class GameSessionRepositoryTest extends BaseRepositoryTest {
    @Resource GameSessionRepository repository;

    @Test
    void saveAndLoad() {
        var userId = randomUUID();
        var gameId = repository.create(userId, REVENUE);

        var result = repository.find(gameId);
        assertThat(result).isEqualTo(
          GameSession.builder().id(gameId).userId(userId).score(0).createdAt(result.createdAt()).finishedAt(null).category(REVENUE).build()
        );
    }
}