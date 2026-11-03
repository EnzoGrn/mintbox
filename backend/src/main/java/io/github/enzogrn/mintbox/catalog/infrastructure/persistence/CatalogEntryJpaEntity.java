package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryDetails;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.LineAttributes;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.catalog.domain.ReleaseDates;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.ColumnTransformer;

/**
 * How a catalog entry is stored. This class only mirrors the tables: the rules live in
 * {@link CatalogEntry}. See {@code docs/adr/0009-framework-free-domain-model.md}.
 */
@Entity
@Table(name = "catalog_entry", schema = "catalog")
class CatalogEntryJpaEntity {

    @Id
    private UUID id;

    /** Optimistic locking: two concurrent updates of the same entry can't both succeed. */
    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_line", nullable = false, updatable = false)
    private ProductLine productLine;

    @Column(name = "source_reference", nullable = false, updatable = false)
    private String sourceReference;

    @Column(nullable = false)
    private String name;

    @Column(name = "character_name", nullable = false)
    private String character;

    @Column(nullable = false)
    private String series;

    @Column(nullable = false)
    private String franchise;

    @Enumerated(EnumType.STRING)
    @Column(name = "figure_type", nullable = false)
    private FigureType figureType;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    /** The JDBC driver sends text: the cast tells PostgreSQL to store it as {@code jsonb}. */
    @ColumnTransformer(write = "?::jsonb")
    @Column(nullable = false, columnDefinition = "jsonb")
    private String attributes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "catalog_entry_release_date",
            schema = "catalog",
            joinColumns = @JoinColumn(name = "entry_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "region")
    @Column(name = "release_date", nullable = false)
    private Map<Region, LocalDate> releaseDates = new HashMap<>();

    /** Required by JPA. */
    protected CatalogEntryJpaEntity() {
    }

    CatalogEntryJpaEntity(CatalogEntry entry) {
        this.id = entry.id().value();
        this.productLine = entry.productLine();
        this.sourceReference = entry.sourceReference().value();
        apply(entry.details());
    }

    /** Copies the entry's details. The identity and the source reference never change. */
    void apply(CatalogEntryDetails details) {
        this.name = details.name();
        this.character = details.character();
        this.series = details.series();
        this.franchise = details.franchise();
        this.figureType = details.figureType();
        this.imageUrl = details.imageUrl().toString();
        this.attributes = AttributesJson.write(details.attributes().values());

        // Only touch the collection when it changed: replacing its content makes Hibernate
        // delete and reinsert every row.
        Map<Region, LocalDate> dates = details.releaseDates().dates();
        if (!releaseDates.equals(dates)) {
            releaseDates.clear();
            releaseDates.putAll(dates);
        }
    }

    CatalogEntry toDomain() {
        return CatalogEntry.restore(
                new CatalogEntryId(id),
                productLine,
                new SourceReference(sourceReference),
                new CatalogEntryDetails(
                        name,
                        character,
                        series,
                        franchise,
                        figureType,
                        new ReleaseDates(releaseDates),
                        URI.create(imageUrl),
                        new LineAttributes(AttributesJson.read(attributes))));
    }
}
