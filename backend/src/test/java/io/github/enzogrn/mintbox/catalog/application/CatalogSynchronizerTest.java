package io.github.enzogrn.mintbox.catalog.application;

import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetails;
import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetailsNamed;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.enzogrn.mintbox.catalog.CatalogEntryAdded;
import io.github.enzogrn.mintbox.catalog.CatalogEntryUpdated;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogSource;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.catalog.domain.SourcedEntry;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CatalogSynchronizerTest {

    private static final SourceReference LINK = new SourceReference("0100000000040002");
    private static final SourceReference ZELDA = new SourceReference("0101000000050002");

    private final InMemoryCatalogEntryRepository repository = new InMemoryCatalogEntryRepository();
    private final List<Object> publishedEvents = new ArrayList<>();
    private final CatalogSynchronizer synchronizer = new CatalogSynchronizer(repository, publishedEvents::add);

    @Test
    void adds_entries_found_only_in_the_source() {
        SynchronizationReport report = synchronizer.synchronize(source(
                new SourcedEntry(LINK, linkDetails()),
                new SourcedEntry(ZELDA, linkDetailsNamed("Zelda"))));

        assertThat(report).isEqualTo(new SynchronizationReport(ProductLine.AMIIBO, 2, 0, 0));
        assertThat(repository.size()).isEqualTo(2);
        assertThat(publishedEvents).hasSize(2).allMatch(CatalogEntryAdded.class::isInstance);
    }

    @Test
    void updates_entries_whose_details_changed_in_the_source() {
        synchronizer.synchronize(source(new SourcedEntry(LINK, linkDetails())));
        publishedEvents.clear();

        SynchronizationReport report = synchronizer.synchronize(
                source(new SourcedEntry(LINK, linkDetailsNamed("Link (renamed)"))));

        assertThat(report).isEqualTo(new SynchronizationReport(ProductLine.AMIIBO, 0, 1, 0));
        CatalogEntry link = repository.findBySourceReference(ProductLine.AMIIBO, LINK).orElseThrow();
        assertThat(link.details().name()).isEqualTo("Link (renamed)");
        assertThat(publishedEvents).containsExactly(new CatalogEntryUpdated(link.id()));
    }

    @Test
    void changes_nothing_when_run_twice_with_the_same_source() {
        CatalogSource source = source(
                new SourcedEntry(LINK, linkDetails()),
                new SourcedEntry(ZELDA, linkDetailsNamed("Zelda")));
        synchronizer.synchronize(source);
        int savesAfterFirstRun = repository.saveCount();
        publishedEvents.clear();

        SynchronizationReport report = synchronizer.synchronize(source);

        assertThat(report).isEqualTo(new SynchronizationReport(ProductLine.AMIIBO, 0, 0, 2));
        assertThat(repository.saveCount()).isEqualTo(savesAfterFirstRun);
        assertThat(publishedEvents).isEmpty();
    }

    @Test
    void keeps_entries_that_disappeared_from_the_source() {
        synchronizer.synchronize(source(
                new SourcedEntry(LINK, linkDetails()),
                new SourcedEntry(ZELDA, linkDetailsNamed("Zelda"))));

        synchronizer.synchronize(source(new SourcedEntry(LINK, linkDetails())));

        // Rule CAT-2: catalog entries are never deleted.
        assertThat(repository.findBySourceReference(ProductLine.AMIIBO, ZELDA)).isPresent();
    }

    @Test
    void clears_the_events_of_each_entry_once_published() {
        synchronizer.synchronize(source(new SourcedEntry(LINK, linkDetails())));

        CatalogEntry link = repository.findBySourceReference(ProductLine.AMIIBO, LINK).orElseThrow();
        assertThat(link.domainEvents()).isEmpty();
    }

    private static CatalogSource source(SourcedEntry... entries) {
        return new FixedCatalogSource(List.of(entries));
    }

    /** A source that always returns the same entries. */
    private record FixedCatalogSource(List<SourcedEntry> entries) implements CatalogSource {

        @Override
        public ProductLine productLine() {
            return ProductLine.AMIIBO;
        }
    }
}
