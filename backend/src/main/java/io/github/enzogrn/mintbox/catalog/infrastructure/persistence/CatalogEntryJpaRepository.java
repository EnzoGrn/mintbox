package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Spring Data access to the catalog tables. Only used by {@link JpaCatalogEntryRepository}. */
interface CatalogEntryJpaRepository extends JpaRepository<CatalogEntryJpaEntity, UUID> {

    Optional<CatalogEntryJpaEntity> findByProductLineAndSourceReference(
            ProductLine productLine, String sourceReference);

    /**
     * Loads the entries with their release dates in a single query. Without the fetch join,
     * Hibernate would run one extra query per entry to load its dates (the "N+1" problem):
     * about 900 queries for the amiibo catalog.
     */
    @Query("""
            select distinct entry from CatalogEntryJpaEntity entry
            left join fetch entry.releaseDates
            where entry.productLine = :productLine
            """)
    List<CatalogEntryJpaEntity> findAllWithReleaseDatesByProductLine(ProductLine productLine);
}
