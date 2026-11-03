package io.github.enzogrn.mintbox;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Starts a real PostgreSQL in Docker for the tests that need a database, and points the
 * application at it. Import it with {@code @Import(TestcontainersConfiguration.class)}.
 *
 * <p>Spring caches the test application context, so all test classes with the same
 * configuration share one container.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    /** Same image as compose.yaml: keep both in sync when upgrading. */
    static final DockerImageName POSTGRES_IMAGE =
            DockerImageName.parse("postgis/postgis:18-3.6").asCompatibleSubstituteFor("postgres");

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(POSTGRES_IMAGE);
    }
}
