package com.shubilet.expedition_service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that need a real database.
 *
 * <p>Starts a throwaway <b>PostgreSQL 15</b> container (matching production) and
 * wires it into the Spring context via {@link ServiceConnection}. Subclasses
 * inherit both the Spring Boot test context and the container lifecycle, so they
 * run against the same database engine the service uses in production rather than
 * an in-memory substitute.
 *
 * <p><b>Requires Docker</b> to be available on the machine running the tests.
 */
@SpringBootTest
@Testcontainers
public abstract class AbstractPostgresIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15");
}
