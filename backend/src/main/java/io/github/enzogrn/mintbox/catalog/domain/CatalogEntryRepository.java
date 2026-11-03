package io.github.enzogrn.mintbox.catalog.domain;

import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.Optional;

/**
 * Stores and retrieves catalog entries.
 *
 * <p>The domain declares what it needs; the infrastructure provides the implementation. See
 * {@code docs/adr/0009-framework-free-domain-model.md}.
 */
public interface CatalogEntryRepository {

    Optional<CatalogEntry> findById(CatalogEntryId id);

    Optional<CatalogEntry> findBySourceReference(ProductLine productLine, SourceReference sourceReference);

    /** Inserts the entry if it's new, updates it otherwise. */
    void save(CatalogEntry entry);
}
