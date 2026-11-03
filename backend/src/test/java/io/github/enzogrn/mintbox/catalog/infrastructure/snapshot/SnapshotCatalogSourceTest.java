package io.github.enzogrn.mintbox.catalog.infrastructure.snapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryDetails;
import io.github.enzogrn.mintbox.catalog.domain.CatalogSourceUnavailableException;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.LineAttributes;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.catalog.domain.ReleaseDates;
import io.github.enzogrn.mintbox.catalog.domain.SourceReference;
import io.github.enzogrn.mintbox.catalog.domain.SourcedEntry;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SnapshotCatalogSourceTest {

    @Nested
    class Reading {

        @Test
        void maps_every_field_of_a_snapshot_entry() {
            List<SourcedEntry> entries = sourceFor("catalog/test-snapshot.json").entries();

            assertThat(entries).first().isEqualTo(new SourcedEntry(
                    new SourceReference("0100000000040002"),
                    new CatalogEntryDetails(
                            "Link",
                            "Link",
                            "Super Smash Bros.",
                            "The Legend of Zelda",
                            FigureType.FIGURE,
                            new ReleaseDates(Map.of(
                                    Region.EUROPE, LocalDate.of(2014, 11, 28),
                                    Region.NORTH_AMERICA, LocalDate.of(2014, 11, 21))),
                            URI.create("https://example.com/images/link.png"),
                            LineAttributes.none())));
        }

        @Test
        void treats_missing_release_dates_and_attributes_as_empty() {
            SourcedEntry blocks = sourceFor("catalog/test-snapshot.json").entries().get(1);

            assertThat(blocks.details().releaseDates()).isEqualTo(ReleaseDates.none());
            assertThat(blocks.details().attributes()).isEqualTo(LineAttributes.none());
            assertThat(blocks.details().figureType()).isEqualTo(FigureType.BLOCK);
        }

        @Test
        void reports_a_missing_file_as_an_unavailable_source() {
            assertThatThrownBy(() -> sourceFor("catalog/does-not-exist.json").entries())
                    .isInstanceOf(CatalogSourceUnavailableException.class);
        }

        @Test
        void reports_a_malformed_file_as_an_unavailable_source() {
            assertThatThrownBy(() -> sourceFor("catalog/malformed-snapshot.json").entries())
                    .isInstanceOf(CatalogSourceUnavailableException.class);
        }
    }

    /**
     * Checks the snapshot shipped with the application, so that a bad edit of the file fails
     * the build instead of the next startup.
     */
    @Nested
    class AmiiboSnapshot {

        private final List<SourcedEntry> entries = sourceFor("catalog/amiibo.json").entries();

        @Test
        void contains_the_whole_amiibo_catalog() {
            // Entries are never removed (rule CAT-2): the catalog can only grow.
            assertThat(entries).hasSizeGreaterThanOrEqualTo(952);
        }

        @Test
        void has_a_unique_source_reference_per_entry() {
            Set<SourceReference> references =
                    entries.stream().map(SourcedEntry::sourceReference).collect(Collectors.toSet());

            assertThat(references).hasSameSizeAs(entries);
        }

        @Test
        void uses_16_digit_hexadecimal_source_references() {
            assertThat(entries).allSatisfy(entry ->
                    assertThat(entry.sourceReference().value()).matches("[0-9a-f]{16}"));
        }

        @Test
        void passes_the_amiibo_product_line_rules() {
            assertThat(entries).allSatisfy(entry ->
                    ProductLine.AMIIBO.validate(entry.details().attributes()));
        }
    }

    private static SnapshotCatalogSource sourceFor(String path) {
        return new SnapshotCatalogSource(ProductLine.AMIIBO, new ClassPathResource(path));
    }
}
