package io.github.enzogrn.mintbox.catalog.domain;

import io.github.enzogrn.mintbox.catalog.CatalogEntryAdded;
import io.github.enzogrn.mintbox.catalog.CatalogEntryUpdated;
import io.github.enzogrn.mintbox.shared.AggregateRoot;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.Objects;

/**
 * A product that exists in the real world, such as one amiibo figure.
 *
 * <p>Catalog entries are reference data: members pick them, they don't create them. Entries
 * are created and updated by catalog synchronizations, and never deleted, because listings
 * and collections reference them (rule CAT-2). See {@code docs/domain/aggregates.md}.
 */
public final class CatalogEntry extends AggregateRoot<CatalogEntryId> {

    private final CatalogEntryId id;
    private final ProductLine productLine;
    private final SourceReference sourceReference;
    private CatalogEntryDetails details;

    private CatalogEntry(
            CatalogEntryId id,
            ProductLine productLine,
            SourceReference sourceReference,
            CatalogEntryDetails details) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.productLine = Objects.requireNonNull(productLine, "productLine must not be null");
        this.sourceReference = Objects.requireNonNull(sourceReference, "sourceReference must not be null");
        this.details = validated(productLine, details);
    }

    /** Adds a new entry to the catalog, found in a catalog source. */
    public static CatalogEntry register(
            ProductLine productLine, SourceReference sourceReference, CatalogEntryDetails details) {
        CatalogEntry entry =
                new CatalogEntry(CatalogEntryId.generate(), productLine, sourceReference, details);
        entry.registerEvent(new CatalogEntryAdded(entry.id));
        return entry;
    }

    /**
     * Rebuilds an existing entry, for example from the database. No event is recorded: nothing
     * happened in the domain, the entry is only loaded.
     */
    public static CatalogEntry restore(
            CatalogEntryId id,
            ProductLine productLine,
            SourceReference sourceReference,
            CatalogEntryDetails details) {
        return new CatalogEntry(id, productLine, sourceReference, details);
    }

    /**
     * Replaces the entry's details with the ones currently found in the catalog source.
     *
     * <p>Synchronizations call this for every entry, most of which haven't changed. An event
     * is only recorded when something actually changed, so that other modules don't refresh
     * their copies for nothing.
     *
     * @return whether the details changed
     */
    public boolean updateDetails(CatalogEntryDetails newDetails) {
        CatalogEntryDetails validated = validated(productLine, newDetails);
        if (validated.equals(details)) {
            return false;
        }
        details = validated;
        registerEvent(new CatalogEntryUpdated(id));
        return true;
    }

    @Override
    public CatalogEntryId id() {
        return id;
    }

    public ProductLine productLine() {
        return productLine;
    }

    public SourceReference sourceReference() {
        return sourceReference;
    }

    public CatalogEntryDetails details() {
        return details;
    }

    private static CatalogEntryDetails validated(ProductLine productLine, CatalogEntryDetails details) {
        Objects.requireNonNull(details, "details must not be null");
        productLine.validate(details.attributes());
        return details;
    }
}
