package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A repository that keeps entries in memory, to test application services without a
 * database. Hand-written rather than mocked: it behaves like a real repository, so tests
 * check outcomes instead of method calls.
 */
class InMemoryCatalogEntryRepository implements CatalogEntryRepository {

    private final Map<CatalogEntryId, CatalogEntry> entries = new LinkedHashMap<>();
    private int saveCount;

    @Override
    public Optional<CatalogEntry> findById(CatalogEntryId id) {
        return Optional.ofNullable(entries.get(id));
    }

    @Override
    public Optional<CatalogEntry> findBySourceReference(ProductLine productLine, SourceReference sourceReference) {
        return entries.values().stream()
                .filter(entry -> entry.productLine() == productLine)
                .filter(entry -> entry.sourceReference().equals(sourceReference))
                .findFirst();
    }

    @Override
    public List<CatalogEntry> findAllByProductLine(ProductLine productLine) {
        return entries.values().stream().filter(entry -> entry.productLine() == productLine).toList();
    }

    @Override
    public void save(CatalogEntry entry) {
        entries.put(entry.id(), entry);
        saveCount++;
    }

    int size() {
        return entries.size();
    }

    /** How many times {@link #save} was called, to check that unchanged entries aren't written. */
    int saveCount() {
        return saveCount;
    }
}
