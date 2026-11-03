package io.github.enzogrn.mintbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Checks that the application starts: every bean can be created, the Flyway migrations
 * apply, and the JPA entities match the resulting schema.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MintboxApplicationTest {

    @Test
    void application_context_starts() {
        // Fails if the context can't start.
    }
}
