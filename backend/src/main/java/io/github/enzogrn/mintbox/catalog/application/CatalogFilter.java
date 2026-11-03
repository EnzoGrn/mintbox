package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;

/**
 * Criteria to browse the catalog. A {@code null} criterion matches everything; series and
 * franchise must match exactly.
 */
public record CatalogFilter(ProductLine productLine, String series, String franchise, FigureType figureType) {

    private static final CatalogFilter NONE = new CatalogFilter(null, null, null, null);

    public CatalogFilter {
        series = blankToNull(series);
        franchise = blankToNull(franchise);
    }

    public static CatalogFilter none() {
        return NONE;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
