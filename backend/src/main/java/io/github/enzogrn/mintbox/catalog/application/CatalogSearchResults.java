package io.github.enzogrn.mintbox.catalog.application;

import java.util.List;

/**
 * The entries matching a search, best match first.
 *
 * <p>Search results aren't paginated: past the first few dozen, results are too far from the
 * query to be useful, and the client should ask for a more precise query instead.
 */
public record CatalogSearchResults(List<CatalogEntrySummary> items) {

    public CatalogSearchResults {
        items = List.copyOf(items);
    }
}
