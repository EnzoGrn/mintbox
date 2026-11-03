package io.github.enzogrn.mintbox.catalog.domain;

import java.net.URI;
import java.util.Objects;

/**
 * Everything a catalog source says about a catalog entry.
 *
 * <p>A synchronization builds a new {@code CatalogEntryDetails} from the source and compares
 * it with the current one: being a record, two details with the same values are equal.
 *
 * @param name         the product name, for example "Link (The Legend of Zelda series)"
 * @param character    the character the figure represents, for example "Link"
 * @param series       the release line, used for collection progress
 * @param franchise    the game universe the character comes from
 * @param figureType   the physical form
 * @param releaseDates the release date in each region
 * @param imageUrl     an HTTPS link to the image at the source; Mintbox doesn't host it
 * @param attributes   attributes specific to the entry's product line
 */
public record CatalogEntryDetails(
        String name,
        String character,
        String series,
        String franchise,
        FigureType figureType,
        ReleaseDates releaseDates,
        URI imageUrl,
        LineAttributes attributes) {

    static final int MAX_TEXT_LENGTH = 200;

    public CatalogEntryDetails {
        name = requireText(name, "name");
        character = requireText(character, "character");
        series = requireText(series, "series");
        franchise = requireText(franchise, "franchise");
        Objects.requireNonNull(figureType, "figureType must not be null");
        Objects.requireNonNull(releaseDates, "releaseDates must not be null");
        requireHttps(imageUrl);
        Objects.requireNonNull(attributes, "attributes must not be null");
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        String stripped = value.strip();
        if (stripped.isEmpty() || stripped.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    field + " must have between 1 and " + MAX_TEXT_LENGTH + " characters");
        }
        return stripped;
    }

    /**
     * The image is loaded by the visitor's browser, so a plain HTTP link would be blocked or
     * flagged on an HTTPS page.
     */
    private static void requireHttps(URI imageUrl) {
        Objects.requireNonNull(imageUrl, "imageUrl must not be null");
        if (!"https".equalsIgnoreCase(imageUrl.getScheme()) || imageUrl.getHost() == null) {
            throw new IllegalArgumentException("imageUrl must be an absolute HTTPS URL: " + imageUrl);
        }
    }
}
