package com.springbootlearning.learningspringboot3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

import com.springbootlearning.learningspringboot3.config.IntegrationTest;
import com.springbootlearning.learningspringboot3.config.TestConfig;
import com.springbootlearning.learningspringboot3.entity.VideoEntity;
import com.springbootlearning.learningspringboot3.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@IntegrationTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestConfig.class)
public class VideoRepositoryTestcontainersTest {

    @Autowired
    VideoRepository repository;

    @BeforeEach
    void setUp() {
        repository.saveAll(
                List.of(
                        new VideoEntity(
                                "alice",
                                "Need HELP with your SPRING BOOT 3 App?",
                                "SPRING BOOT 3 will only speed things up."),
                        new VideoEntity("alice",
                                "Don't do THIS to your own CODE!",
                                "As a pro developer, never ever EVER do this to your code."),
                        new VideoEntity("bob",
                                "SECRETS to fix BROKEN CODE!",
                                "Discover ways to not only debug your code")));
    }

    @Test
    void findAllShouldProduceAllVideos() {
        List<VideoEntity> videos = repository.findAll();
        assertThat(videos).hasSize(3);
    }

    @Test
    void findByName() {
        List<VideoEntity> videos = repository.findByNameContainsIgnoreCase("SPRING BOOT 3");
        assertThat(videos).hasSize(1);
    }

    @Test
    void findByNameOrDescription() {
        List<VideoEntity> videos =
                repository.findByNameContainsOrDescriptionContainsAllIgnoreCase("CODE", "your code");
        assertThat(videos).hasSize(2);
    }
}
