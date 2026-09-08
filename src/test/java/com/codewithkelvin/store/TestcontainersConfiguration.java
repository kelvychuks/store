package com.codewithkelvin.store;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Integration tests run against a real PostgreSQL container rather than an
 * in-memory database, because the thing most worth testing here is that the
 * Flyway migrations apply cleanly and that the entities still match the schema
 * they produce. H2 would happily accept a schema PostgreSQL rejects.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine");
    }
}
