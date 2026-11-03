package io.github.enzogrn.mintbox.catalog.application;

import io.github.enzogrn.mintbox.catalog.domain.ProductLine;

/**
 * What a catalog synchronization did.
 *
 * @param productLine the product line that was synchronized
 * @param added       entries found in the source and not yet in the catalog
 * @param updated     entries whose details changed in the source
 * @param unchanged   entries identical in the source and in the catalog
 */
public record SynchronizationReport(ProductLine productLine, int added, int updated, int unchanged) {

    public int total() {
        return added + updated + unchanged;
    }
}
