package io.github.enzogrn.mintbox.catalog.domain;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

/** Ready-made catalog data for tests. */
final class CatalogEntryFixtures {

    static final SourceReference LINK_REFERENCE = new SourceReference("01000000034c0902");

    private CatalogEntryFixtures() {
    }

    static CatalogEntryDetails linkDetails() {
        return new CatalogEntryDetails(
                "Link",
                "Link",
                "Super Smash Bros.",
                "The Legend of Zelda",
                FigureType.FIGURE,
                new ReleaseDates(Map.of(
                        Region.EUROPE, LocalDate.of(2014, 11, 28),
                        Region.NORTH_AMERICA, LocalDate.of(2014, 11, 21))),
                URI.create("https://example.com/images/link.png"),
                LineAttributes.none());
    }

    static CatalogEntryDetails linkDetailsNamed(String name) {
        CatalogEntryDetails link = linkDetails();
        return new CatalogEntryDetails(
                name,
                link.character(),
                link.series(),
                link.franchise(),
                link.figureType(),
                link.releaseDates(),
                link.imageUrl(),
                link.attributes());
    }
}
