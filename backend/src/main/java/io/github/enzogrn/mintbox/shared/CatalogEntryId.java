package io.github.enzogrn.mintbox.shared;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifies a catalog entry.
 *
 * <p>It lives in {@code shared} rather than in the catalog module because listings,
 * collections and orders all reference catalog entries.
 */
public record CatalogEntryId(UUID value) {

    public CatalogEntryId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CatalogEntryId generate() {
        return new CatalogEntryId(UuidV7.generate());
    }

    public static CatalogEntryId of(String value) {
        return new CatalogEntryId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
