package io.github.enzogrn.mintbox.catalog.domain;

import java.util.List;

/**
 * Where the catalog entries of one product line come from: a versioned file, an external
 * API... The domain only knows this interface. See
 * {@code docs/adr/0006-multi-product-line-catalog.md}.
 */
public interface CatalogSource {

    ProductLine productLine();

    /**
     * Returns every entry the source currently knows about, for {@link #productLine()}.
     *
     * @throws CatalogSourceUnavailableException if the source can't be read
     */
    List<SourcedEntry> entries();
}
