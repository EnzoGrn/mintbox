package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Implements the domain's {@link CatalogEntryRepository} with JPA, mapping between the
 * domain objects and {@link CatalogEntryJpaEntity}.
 *
 * <p>Callers are expected to load and save an entry within one transaction, as application
 * services do: the JPA entity loaded first is the one updated, so its version number
 * detects concurrent changes.
 */
@Component
class JpaCatalogEntryRepository implements CatalogEntryRepository {

    private final CatalogEntryJpaRepository jpaRepository;

    JpaCatalogEntryRepository(CatalogEntryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<CatalogEntry> findById(CatalogEntryId id) {
        return jpaRepository.findById(id.value()).map(CatalogEntryJpaEntity::toDomain);
    }

    @Override
    public Optional<CatalogEntry> findBySourceReference(
            ProductLine productLine, SourceReference sourceReference) {
        return jpaRepository
                .findByProductLineAndSourceReference(productLine, sourceReference.value())
                .map(CatalogEntryJpaEntity::toDomain);
    }

    @Override
    public void save(CatalogEntry entry) {
        CatalogEntryJpaEntity entity = jpaRepository.findById(entry.id().value())
                .map(existing -> {
                    existing.apply(entry.details());
                    return existing;
                })
                .orElseGet(() -> new CatalogEntryJpaEntity(entry));
        jpaRepository.save(entity);
    }
}
