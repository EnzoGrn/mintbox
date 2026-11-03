package io.github.enzogrn.mintbox.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.enzogrn.mintbox.TestcontainersConfiguration;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.CatalogSource;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/**
 * Imports the real amiibo snapshot into a real PostgreSQL, as the application does at
 * startup. The transaction is rolled back at the end, leaving the database empty.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class CatalogSynchronizerIntegrationTest {

    @Autowired
    private CatalogSynchronizer synchronizer;

    @Autowired
    private CatalogSource amiiboSnapshot;

    @Autowired
    private CatalogEntryRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void imports_the_whole_amiibo_snapshot_then_finds_nothing_to_change() {
        SynchronizationReport firstRun = synchronizer.synchronize(amiiboSnapshot);
        entityManager.flush();
        entityManager.clear();

        SynchronizationReport secondRun = synchronizer.synchronize(amiiboSnapshot);

        assertThat(firstRun.added()).isEqualTo(firstRun.total()).isGreaterThanOrEqualTo(952);
        assertThat(secondRun.unchanged()).isEqualTo(firstRun.total());
        assertThat(secondRun.added()).isZero();
        assertThat(secondRun.updated()).isZero();
        assertThat(repository.findAllByProductLine(ProductLine.AMIIBO)).hasSize(firstRun.total());
    }
}
