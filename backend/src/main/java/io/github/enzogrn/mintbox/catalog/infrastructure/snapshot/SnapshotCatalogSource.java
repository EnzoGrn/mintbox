package io.github.enzogrn.mintbox.catalog.infrastructure.snapshot;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryDetails;
import io.github.enzogrn.mintbox.catalog.domain.CatalogSource;
import io.github.enzogrn.mintbox.catalog.domain.CatalogSourceUnavailableException;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.LineAttributes;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.catalog.domain.ReleaseDates;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.catalog.domain.SourcedEntry;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Reads a catalog snapshot: a JSON file versioned in the repository, listing every entry of
 * one product line. See {@code docs/adr/0011-amiibo-snapshot-as-source-of-truth.md}.
 *
 * <p>The file is read every time {@link #entries()} is called, so that it never stays in
 * memory after a synchronization.
 */
public class SnapshotCatalogSource implements CatalogSource {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private final ProductLine productLine;
    private final Resource snapshot;

    public SnapshotCatalogSource(ProductLine productLine, Resource snapshot) {
        this.productLine = productLine;
        this.snapshot = snapshot;
    }

    @Override
    public ProductLine productLine() {
        return productLine;
    }

    @Override
    public List<SourcedEntry> entries() {
        SnapshotFile file = read();
        if (file.productLine() != productLine) {
            throw new CatalogSourceUnavailableException(
                    "Snapshot " + snapshot.getDescription() + " is for product line " + file.productLine()
                            + ", expected " + productLine,
                    null);
        }
        return file.entries().stream().map(SnapshotCatalogSource::toSourcedEntry).toList();
    }

    private SnapshotFile read() {
        try (InputStream input = snapshot.getInputStream()) {
            return MAPPER.readValue(input, SnapshotFile.class);
        } catch (IOException | JacksonException e) {
            throw new CatalogSourceUnavailableException("Can't read snapshot " + snapshot.getDescription(), e);
        }
    }

    private static SourcedEntry toSourcedEntry(SnapshotEntry entry) {
        return new SourcedEntry(
                new SourceReference(entry.sourceReference()),
                new CatalogEntryDetails(
                        entry.name(),
                        entry.character(),
                        entry.series(),
                        entry.franchise(),
                        entry.figureType(),
                        new ReleaseDates(entry.releaseDates() == null ? Map.of() : entry.releaseDates()),
                        entry.imageUrl(),
                        entry.attributes() == null ? LineAttributes.none() : new LineAttributes(entry.attributes())));
    }

    /** The structure of a snapshot file. */
    record SnapshotFile(ProductLine productLine, String source, List<SnapshotEntry> entries) {
    }

    /** One entry of a snapshot file. {@code releaseDates} and {@code attributes} are optional. */
    record SnapshotEntry(
            String sourceReference,
            String name,
            String character,
            String series,
            String franchise,
            FigureType figureType,
            Map<Region, LocalDate> releaseDates,
            URI imageUrl,
            Map<String, String> attributes) {
    }
}
