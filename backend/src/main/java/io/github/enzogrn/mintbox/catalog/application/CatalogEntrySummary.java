package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import java.net.URI;
import java.util.UUID;

/** What a list of catalog entries shows about each entry. */
public record CatalogEntrySummary(
        UUID id,
        String name,
        String character,
        String series,
        String franchise,
        FigureType figureType,
        URI imageUrl) {
}
