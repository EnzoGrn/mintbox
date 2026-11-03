package io.github.enzogrn.mintbox.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UuidV7Test {

    private static final Instant NOW = Instant.parse("2026-11-03T15:00:00Z");

    @Test
    void sets_the_version_and_the_variant() {
        UUID uuid = UuidV7.generate();

        assertThat(uuid.version()).isEqualTo(7);
        assertThat(uuid.variant()).isEqualTo(2);
    }

    @Test
    void stores_the_creation_time_in_the_first_48_bits() {
        UUID uuid = UuidV7.generate(Clock.fixed(NOW, ZoneOffset.UTC));

        assertThat(uuid.getMostSignificantBits() >>> 16).isEqualTo(NOW.toEpochMilli());
    }

    @Test
    void sorts_identifiers_by_creation_time() {
        UUID earlier = UuidV7.generate(Clock.fixed(NOW, ZoneOffset.UTC));
        UUID later = UuidV7.generate(Clock.fixed(NOW.plusMillis(1), ZoneOffset.UTC));

        assertThat(earlier).isLessThan(later);
    }

    @Test
    void generates_different_identifiers_within_the_same_millisecond() {
        Clock frozen = Clock.fixed(NOW, ZoneOffset.UTC);
        Set<UUID> generated = new HashSet<>();

        for (int i = 0; i < 10_000; i++) {
            generated.add(UuidV7.generate(frozen));
        }

        assertThat(generated).hasSize(10_000);
    }
}
