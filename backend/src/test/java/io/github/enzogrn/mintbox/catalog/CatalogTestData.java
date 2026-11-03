package io.github.enzogrn.mintbox.catalog;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryDetails;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.LineAttributes;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.catalog.domain.ReleaseDates;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import jakarta.persistence.EntityManager;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creates catalog entries in the database for integration tests, through the real
 * repository. Call {@link #flush()} before reading them back with SQL.
 */
public final class CatalogTestData {

    private static final AtomicInteger SEQUENCE = new AtomicInteger();

    private final CatalogEntryRepository repository;
    private final EntityManager entityManager;

    public CatalogTestData(CatalogEntryRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    public CatalogEntry add(String name, String series, String franchise, FigureType figureType) {
        CatalogEntry entry = CatalogEntry.register(
                ProductLine.AMIIBO,
                new SourceReference("test-%06d".formatted(SEQUENCE.incrementAndGet())),
                new CatalogEntryDetails(
                        name,
                        name,
                        series,
                        franchise,
                        figureType,
                        new ReleaseDates(Map.of(Region.EUROPE, LocalDate.of(2014, 11, 28))),
                        URI.create("https://example.com/images/" + SEQUENCE.get() + ".png"),
                        LineAttributes.none()));
        repository.save(entry);
        return entry;
    }

    /** Writes pending entries to the database, so that SQL queries see them. */
    public void flush() {
        entityManager.flush();
    }
}
