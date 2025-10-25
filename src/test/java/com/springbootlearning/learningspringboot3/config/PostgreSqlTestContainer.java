package com.springbootlearning.learningspringboot3.config;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSqlTestContainer implements SqlTestContainer {

    private PostgreSQLContainer<?> postgreSQLContainer;

    @Override
    public void destroy() {
        if (null != postgreSQLContainer && postgreSQLContainer.isRunning()) {
            postgreSQLContainer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (null == postgreSQLContainer) {
            postgreSQLContainer = new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("sampleApplication")
                    .withReuse(true);
        }
        if (!postgreSQLContainer.isRunning()) {
            postgreSQLContainer.start();
        }
    }

    @Override
    public JdbcDatabaseContainer<?> getTestContainer() {
        return postgreSQLContainer;
    }
}
