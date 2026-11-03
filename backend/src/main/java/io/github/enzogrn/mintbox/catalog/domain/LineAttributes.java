package io.github.enzogrn.mintbox.catalog.domain;

import java.util.Map;
import java.util.Objects;

/**
 * Attributes specific to one product line, such as the element of a Skylander.
 *
 * <p>Which names are allowed depends on the product line: see
 * {@link ProductLine#validate(LineAttributes)}.
 *
 * @param values attribute names and values; the map is copied and can't be modified
 */
public record LineAttributes(Map<String, String> values) {

    private static final LineAttributes NONE = new LineAttributes(Map.of());

    public LineAttributes {
        Objects.requireNonNull(values, "values must not be null");
        values.forEach((name, value) -> {
            if (name == null || name.isBlank() || value == null || value.isBlank()) {
                throw new IllegalArgumentException(
                        "Attribute names and values must not be blank: " + name + "=" + value);
            }
        });
        values = Map.copyOf(values);
    }

    public static LineAttributes none() {
        return NONE;
    }
}
