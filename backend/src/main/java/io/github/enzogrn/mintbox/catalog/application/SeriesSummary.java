package io.github.enzogrn.mintbox.catalog.application;

/**
 * A series and its size, to navigate the catalog and, later, to compute collection progress.
 *
 * @param name       the series name, as stored on catalog entries
 * @param entryCount how many catalog entries belong to the series
 */
public record SeriesSummary(String name, int entryCount) {
}
