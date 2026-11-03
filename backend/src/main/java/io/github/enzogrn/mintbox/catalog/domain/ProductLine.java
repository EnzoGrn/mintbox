package io.github.enzogrn.mintbox.catalog.domain;

import java.util.Set;
import java.util.TreeSet;

/**
 * A family of NFC figures from one manufacturer.
 *
 * <p>Each product line declares the line-specific attributes its entries may have, on top
 * of the attributes shared by every line. See
 * {@code docs/adr/0006-multi-product-line-catalog.md}.
 */
public enum ProductLine {

    /** Nintendo's figures. Every amiibo attribute is a shared one, so there is none to add. */
    AMIIBO(Set.of());

    private final Set<String> attributeNames;

    ProductLine(Set<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public Set<String> attributeNames() {
        return attributeNames;
    }

    /**
     * Checks that the given attributes are all declared by this product line.
     *
     * @throws IllegalArgumentException listing the unknown attributes, if any
     */
    public void validate(LineAttributes attributes) {
        Set<String> unknown = new TreeSet<>(attributes.values().keySet());
        unknown.removeAll(attributeNames);
        if (!unknown.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unknown attributes for product line " + this + ": " + unknown);
        }
    }
}
