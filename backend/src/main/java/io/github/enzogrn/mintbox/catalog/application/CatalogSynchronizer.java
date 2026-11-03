package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.CatalogSource;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.catalog.domain.SourcedEntry;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Brings the catalog of one product line in line with what its source says: adds the new
 * entries and updates the changed ones.
 *
 * <p>Running it twice in a row changes nothing the second time, so it is safe to run at every
 * startup. Entries missing from the source are left untouched: catalog entries are never
 * deleted (rule CAT-2).
 */
@Service
public class CatalogSynchronizer {

    private final CatalogEntryRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CatalogSynchronizer(CatalogEntryRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Synchronizes the catalog with the given source, in a single transaction: if any entry is
     * invalid, nothing is changed.
     */
    @Transactional
    public SynchronizationReport synchronize(CatalogSource source) {
        // One query for the whole product line, instead of one per sourced entry.
        Map<SourceReference, CatalogEntry> existing = repository.findAllByProductLine(source.productLine())
                .stream()
                .collect(Collectors.toMap(CatalogEntry::sourceReference, Function.identity()));

        int added = 0;
        int updated = 0;
        int unchanged = 0;

        for (SourcedEntry sourced : source.entries()) {
            CatalogEntry entry = existing.get(sourced.sourceReference());
            if (entry == null) {
                entry = CatalogEntry.register(source.productLine(), sourced.sourceReference(), sourced.details());
                added++;
            } else if (entry.updateDetails(sourced.details())) {
                updated++;
            } else {
                unchanged++;
                continue;
            }
            repository.save(entry);
            publishEventsOf(entry);
        }

        return new SynchronizationReport(source.productLine(), added, updated, unchanged);
    }

    /**
     * Publishes the events the entry recorded. They are published inside the transaction, so
     * listeners registered for after-commit only receive them if the synchronization succeeds.
     */
    private void publishEventsOf(CatalogEntry entry) {
        entry.domainEvents().forEach(eventPublisher::publishEvent);
        entry.clearDomainEvents();
    }
}
