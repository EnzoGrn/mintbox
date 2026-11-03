package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import io.github.enzogrn.mintbox.catalog.application.InvalidCursorException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * The position of the last entry of a page, in the browse order (name, then identifier).
 *
 * <p>Clients receive it as an opaque string, so its format can change without breaking them.
 * The identifier breaks ties between entries with the same name, which are frequent: there
 * are many "Link" and "Mario" amiibo.
 */
record BrowseCursor(String name, UUID id) {

    private static final int UUID_LENGTH = 36;
    private static final char SEPARATOR = ':';

    /** Encodes the identifier first: it has a fixed length, so the name can contain anything. */
    String encode() {
        String raw = id + String.valueOf(SEPARATOR) + name;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    static BrowseCursor decode(String cursor) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            if (raw.length() <= UUID_LENGTH || raw.charAt(UUID_LENGTH) != SEPARATOR) {
                throw new IllegalArgumentException("Unexpected cursor format");
            }
            return new BrowseCursor(raw.substring(UUID_LENGTH + 1), UUID.fromString(raw.substring(0, UUID_LENGTH)));
        } catch (IllegalArgumentException e) {
            throw new InvalidCursorException(cursor, e);
        }
    }
}
