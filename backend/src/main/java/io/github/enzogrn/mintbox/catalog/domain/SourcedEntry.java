package io.github.enzogrn.mintbox.catalog.domain;

import java.util.Objects;

/**
 * One entry as a catalog source describes it, before it is matched with the catalog.
 *
 * @param sourceReference the entry's identifier in the source
 * @param details         everything the source says about the entry
 */
public record SourcedEntry(SourceReference sourceReference, CatalogEntryDetails details) {

    public SourcedEntry {
        Objects.requireNonNull(sourceReference, "sourceReference must not be null");
        Objects.requireNonNull(details, "details must not be null");
    }
}
