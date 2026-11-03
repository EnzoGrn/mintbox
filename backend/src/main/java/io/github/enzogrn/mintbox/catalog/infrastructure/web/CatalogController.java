package io.github.enzogrn.mintbox.catalog.infrastructure.web;

import io.github.enzogrn.mintbox.catalog.application.CatalogEntryView;
import io.github.enzogrn.mintbox.catalog.application.CatalogFilter;
import io.github.enzogrn.mintbox.catalog.application.CatalogPage;
import io.github.enzogrn.mintbox.catalog.application.CatalogQueries;
import io.github.enzogrn.mintbox.catalog.application.InvalidCursorException;
import io.github.enzogrn.mintbox.catalog.application.SeriesSummary;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public, read-only HTTP API of the catalog.
 *
 * <p>Errors are returned as RFC 9457 problem details: unknown entry (404), invalid filter,
 * limit or cursor (400).
 */
@RestController
@RequestMapping("/api/catalog")
class CatalogController {

    private static final String DEFAULT_PAGE_SIZE = "24";

    private final CatalogQueries queries;

    CatalogController(CatalogQueries queries) {
        this.queries = queries;
    }

    /**
     * Lists entries sorted by name, one page at a time. To get the next page, pass the
     * {@code nextCursor} of the response back as {@code cursor}, with the same filters.
     */
    @GetMapping("/entries")
    CatalogPage browse(
            @RequestParam(required = false) ProductLine productLine,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) String franchise,
            @RequestParam(required = false) FigureType figureType,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Min(1) @Max(CatalogQueries.MAX_PAGE_SIZE) int limit) {
        return queries.browse(new CatalogFilter(productLine, series, franchise, figureType), cursor, limit);
    }

    @GetMapping("/entries/{id}")
    CatalogEntryView entry(@PathVariable UUID id) {
        return queries.findById(new CatalogEntryId(id))
                .orElseThrow(() -> new ErrorResponseException(
                        HttpStatus.NOT_FOUND,
                        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "No catalog entry with id " + id),
                        null));
    }

    @GetMapping("/series")
    List<SeriesSummary> series(
            @RequestParam(required = false) ProductLine productLine,
            @RequestParam(required = false) FigureType figureType) {
        return queries.series(new CatalogFilter(productLine, null, null, figureType));
    }

    @ExceptionHandler(InvalidCursorException.class)
    ProblemDetail invalidCursor(InvalidCursorException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "The cursor is invalid: restart from the first page, without a cursor.");
    }
}
