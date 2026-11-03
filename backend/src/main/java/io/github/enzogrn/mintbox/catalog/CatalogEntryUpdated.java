package io.github.enzogrn.mintbox.catalog;

import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.Objects;

/**
 * The details of a catalog entry changed (name, series, image...).
 *
 * <p>Modules that keep a copy of catalog fields, such as listings for search, refresh it
 * when they receive this event.
 */
public record CatalogEntryUpdated(CatalogEntryId entryId) {

    public CatalogEntryUpdated {
        Objects.requireNonNull(entryId, "entryId must not be null");
    }
}
