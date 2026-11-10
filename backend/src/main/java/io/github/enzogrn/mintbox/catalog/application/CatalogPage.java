package io.github.enzogrn.mintbox.catalog.application;

import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * One page of catalog entries.
 *
 * @param items      the entries of this page
 * @param nextCursor pass it back to get the next page; {@code null} on the last page
 */
public record CatalogPage(List<CatalogEntrySummary> items, @Nullable String nextCursor) {

    public CatalogPage {
        items = List.copyOf(items);
    }
}
