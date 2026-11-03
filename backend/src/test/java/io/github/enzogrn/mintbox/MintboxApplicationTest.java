package io.github.enzogrn.mintbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Checks that the Spring application context starts with the default configuration. */
@SpringBootTest
class MintboxApplicationTest {

    @Test
    void application_context_starts() {
        // Fails if any bean can't be created.
    }
}
