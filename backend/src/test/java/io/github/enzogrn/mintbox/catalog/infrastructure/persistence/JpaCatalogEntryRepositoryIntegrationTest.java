package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.LINK_REFERENCE;
import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.enzogrn.mintbox.TestcontainersConfiguration;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryDetails;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.catalog.domain.ReleaseDates;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/**
 * Runs the repository against a real PostgreSQL, with the schema created by the Flyway
 * migrations. Each test runs in a transaction that is rolled back at the end.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class JpaCatalogEntryRepositoryIntegrationTest {

    @Autowired
    private CatalogEntryRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saves_an_entry_and_reads_back_every_field() {
        CatalogEntry entry = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());

        repository.save(entry);
        flushAndClear();

        CatalogEntry reloaded = repository.findById(entry.id()).orElseThrow();
        assertThat(reloaded.id()).isEqualTo(entry.id());
        assertThat(reloaded.productLine()).isEqualTo(ProductLine.AMIIBO);
        assertThat(reloaded.sourceReference()).isEqualTo(LINK_REFERENCE);
        assertThat(reloaded.details()).isEqualTo(linkDetails());
    }

    @Test
    void finds_an_entry_by_its_source_reference_within_a_product_line() {
        CatalogEntry entry = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());
        repository.save(entry);
        flushAndClear();

        assertThat(repository.findBySourceReference(ProductLine.AMIIBO, LINK_REFERENCE))
                .map(CatalogEntry::id)
                .contains(entry.id());
        assertThat(repository.findBySourceReference(ProductLine.AMIIBO, new SourceReference("unknown")))
                .isEmpty();
    }

    @Test
    void finds_nothing_for_an_unknown_identifier() {
        assertThat(repository.findById(CatalogEntryId.generate())).isEmpty();
    }

    @Test
    void saves_changed_details_of_an_existing_entry() {
        CatalogEntry entry = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());
        repository.save(entry);
        flushAndClear();

        CatalogEntry loaded = repository.findById(entry.id()).orElseThrow();
        CatalogEntryDetails changed = new CatalogEntryDetails(
                "Link (Breath of the Wild)",
                "Link",
                "The Legend of Zelda",
                "The Legend of Zelda",
                loaded.details().figureType(),
                // Japan added, North America removed.
                new ReleaseDates(Map.of(
                        Region.EUROPE, LocalDate.of(2017, 3, 3),
                        Region.JAPAN, LocalDate.of(2017, 3, 3))),
                URI.create("https://example.com/images/link-botw.png"),
                loaded.details().attributes());
        loaded.updateDetails(changed);
        repository.save(loaded);
        flushAndClear();

        assertThat(repository.findById(entry.id()).orElseThrow().details()).isEqualTo(changed);
    }

    @Test
    void refuses_two_entries_with_the_same_source_reference_in_a_product_line() {
        repository.save(CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails()));
        repository.save(CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails()));

        assertThatThrownBy(entityManager::flush)
                .isInstanceOf(PersistenceException.class)
                .rootCause()
                .hasMessageContaining("catalog_entry_source_reference_uk");
    }

    /** Sends pending changes to the database and forgets loaded entities, to force real reads. */
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
