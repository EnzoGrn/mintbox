package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/** Everything the page of one catalog entry shows. */
public record CatalogEntryView(
        UUID id,
        ProductLine productLine,
        String name,
        String character,
        String series,
        String franchise,
        FigureType figureType,
        Map<Region, LocalDate> releaseDates,
        URI imageUrl) {

    public CatalogEntryView {
        releaseDates = Map.copyOf(releaseDates);
    }
}
