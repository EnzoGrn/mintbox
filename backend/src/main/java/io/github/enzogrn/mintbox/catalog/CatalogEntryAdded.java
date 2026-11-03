package io.github.enzogrn.mintbox.catalog;

import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.Objects;

/** A new catalog entry was added, usually by a catalog synchronization. */
public record CatalogEntryAdded(CatalogEntryId entryId) {

    public CatalogEntryAdded {
        Objects.requireNonNull(entryId, "entryId must not be null");
    }
}
