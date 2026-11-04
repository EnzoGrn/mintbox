package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.List;
import java.util.Optional;

/**
 * Read-only access to the catalog, for browsing.
 *
 * <p>Queries don't load {@link io.github.enzogrn.mintbox.catalog.domain.CatalogEntry}
 * aggregates: they read the tables directly and return views shaped for display. See
 * {@code docs/adr/0012-read-queries-bypass-the-domain-model.md}.
 */
public interface CatalogQueries {

    int MAX_PAGE_SIZE = 100;

    /**
     * Returns one page of entries matching the filter, sorted by name.
     *
     * @param filter the criteria; empty criteria match everything
     * @param cursor where the previous page ended, as returned in {@link CatalogPage#nextCursor()};
     *               {@code null} for the first page
     * @param limit  the maximum number of entries, between 1 and {@link #MAX_PAGE_SIZE}
     * @throws InvalidCursorException if the cursor wasn't produced by this method
     */
    CatalogPage browse(CatalogFilter filter, String cursor, int limit);

    /**
     * Finds the entries whose name, character, series or franchise contain every word of the
     * query, tolerating typos and ignoring case and accents. "pikachuu" finds Pikachu, and
     * "pokemon" finds "Pokémon".
     *
     * @param query  the words typed by the member; see {@link SearchWords} for how it is split
     * @param filter criteria the results must also match
     * @param limit  the maximum number of results, between 1 and {@link #MAX_PAGE_SIZE}
     * @return the best matches first; nothing if the query has no word long enough
     */
    CatalogSearchResults search(String query, CatalogFilter filter, int limit);

    Optional<CatalogEntryView> findById(CatalogEntryId id);

    /** Returns every series with its number of entries, sorted by name. */
    List<SeriesSummary> series(CatalogFilter filter);
}
