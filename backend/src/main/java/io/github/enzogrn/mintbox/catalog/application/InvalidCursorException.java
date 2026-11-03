package io.github.enzogrn.mintbox.catalog.application;

/** A pagination cursor was not produced by {@link CatalogQueries#browse}, or was altered. */
public class InvalidCursorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidCursorException(String cursor, Throwable cause) {
        super("Invalid pagination cursor: " + cursor, cause);
    }
}
