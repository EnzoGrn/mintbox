package io.github.enzogrn.mintbox.catalog.domain;

import java.util.Objects;

/**
 * The identifier of a catalog entry in its catalog source, used to match entries during a
 * synchronization. For amiibo, it is the figure's 16-character hexadecimal identifier.
 *
 * <p>It is unique within a product line (rule CAT-1), not across product lines.
 */
public record SourceReference(String value) {

    static final int MAX_LENGTH = 64;

    public SourceReference {
        Objects.requireNonNull(value, "value must not be null");
        value = value.strip();
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "A source reference must have between 1 and " + MAX_LENGTH + " characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
