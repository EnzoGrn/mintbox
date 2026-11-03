package io.github.enzogrn.mintbox.shared;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;

/**
 * Generates time-ordered UUIDs (version 7, RFC 9562).
 *
 * <p>The first 48 bits hold the creation time in milliseconds, so identifiers created later
 * sort after earlier ones. This keeps database indexes compact, unlike random UUIDs (version
 * 4), which scatter inserts across the whole index. The remaining bits are random.
 *
 * <p>Two identifiers created in the same millisecond are not ordered between themselves.
 * Nothing in Mintbox relies on that order.
 */
public final class UuidV7 {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final long VERSION_7 = 0x7000L;
    private static final long VARIANT_MASK = 0x3FFF_FFFF_FFFF_FFFFL;
    private static final long VARIANT_RFC_9562 = 0x8000_0000_0000_0000L;

    private UuidV7() {
    }

    public static UUID generate() {
        return generate(Clock.systemUTC());
    }

    static UUID generate(Clock clock) {
        byte[] random = new byte[10];
        RANDOM.nextBytes(random);

        // Most significant bits: 48-bit timestamp, 4-bit version, 12 random bits.
        long timestamp = clock.millis();
        long randomA = ((random[0] & 0x0FL) << 8) | (random[1] & 0xFFL);
        long mostSignificantBits = (timestamp << 16) | VERSION_7 | randomA;

        // Least significant bits: 2-bit variant, 62 random bits.
        long randomB = ByteBuffer.wrap(random, 2, 8).getLong();
        long leastSignificantBits = (randomB & VARIANT_MASK) | VARIANT_RFC_9562;

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
