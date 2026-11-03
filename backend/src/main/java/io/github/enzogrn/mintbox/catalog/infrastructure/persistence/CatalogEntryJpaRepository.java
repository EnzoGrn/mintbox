package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data access to the catalog tables. Only used by {@link JpaCatalogEntryRepository}. */
interface CatalogEntryJpaRepository extends JpaRepository<CatalogEntryJpaEntity, UUID> {

    Optional<CatalogEntryJpaEntity> findByProductLineAndSourceReference(
            ProductLine productLine, String sourceReference);
}
