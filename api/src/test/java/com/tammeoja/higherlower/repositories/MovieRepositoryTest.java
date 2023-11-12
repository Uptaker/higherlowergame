package com.tammeoja.higherlower.repositories;

import com.tammeoja.higherlower.utils.BaseRepositoryTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import static com.tammeoja.higherlower.utils.TestData.testMovie;
import static org.assertj.core.api.Assertions.assertThat;

class MovieRepositoryTest extends BaseRepositoryTest {

    @Resource
    MovieRepository repository;

    @Test
    void save_and_load() {
        var id = repository.save(testMovie);

        assertThat(repository.find(id)).isEqualTo(testMovie.withId(id));
    }
}