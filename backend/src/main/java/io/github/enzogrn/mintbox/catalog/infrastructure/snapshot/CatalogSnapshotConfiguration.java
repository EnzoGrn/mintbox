package io.github.enzogrn.mintbox.catalog.infrastructure.snapshot;

import io.github.enzogrn.mintbox.catalog.application.CatalogSynchronizer;
import io.github.enzogrn.mintbox.catalog.application.SynchronizationReport;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Declares the amiibo snapshot as a catalog source, and synchronizes the catalog with it when
 * the application starts.
 *
 * <p>Since a synchronization only writes what changed, running it at every startup is cheap,
 * and deploying a new version of the snapshot is enough to update the catalog.
 */
@Configuration(proxyBeanMethods = false)
class CatalogSnapshotConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogSnapshotConfiguration.class);

    @Bean
    SnapshotCatalogSource amiiboSnapshot(@Value("classpath:catalog/amiibo.json") Resource snapshot) {
        return new SnapshotCatalogSource(ProductLine.AMIIBO, snapshot);
    }

    /** Disabled in tests, which create the catalog data they need. */
    @Bean
    @ConditionalOnProperty(name = "mintbox.catalog.import-on-startup", havingValue = "true", matchIfMissing = true)
    ApplicationRunner catalogSnapshotImport(CatalogSynchronizer synchronizer, SnapshotCatalogSource amiiboSnapshot) {
        return arguments -> {
            SynchronizationReport report = synchronizer.synchronize(amiiboSnapshot);
            LOG.info("Catalog {} synchronized from snapshot: {} added, {} updated, {} unchanged",
                    report.productLine(), report.added(), report.updated(), report.unchanged());
        };
    }
}
