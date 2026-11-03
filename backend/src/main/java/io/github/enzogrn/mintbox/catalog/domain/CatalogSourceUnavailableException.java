package io.github.enzogrn.mintbox.catalog.domain;

/** A catalog source couldn't be read: missing file, malformed content, unreachable API... */
public class CatalogSourceUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CatalogSourceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
