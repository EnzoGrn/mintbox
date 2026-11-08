package io.github.enzogrn.mintbox.catalog.infrastructure.web;

import io.github.enzogrn.mintbox.catalog.application.CatalogEntryView;
import io.github.enzogrn.mintbox.catalog.application.CatalogFilter;
import io.github.enzogrn.mintbox.catalog.application.CatalogPage;
import io.github.enzogrn.mintbox.catalog.application.CatalogQueries;
import io.github.enzogrn.mintbox.catalog.application.CatalogSearchResults;
import io.github.enzogrn.mintbox.catalog.application.InvalidCursorException;
import io.github.enzogrn.mintbox.catalog.application.SeriesSummary;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
 * limit, cursor or search query (400). The annotations describe the API in its OpenAPI
 * document; the behavior comes from the Spring annotations alone.
 */
@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Catalog", description = "Every figure that exists, by product line. Read-only.")
@ApiResponse(
        responseCode = "400",
        description = "Invalid parameter",
        content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
class CatalogController {

    private static final String DEFAULT_PAGE_SIZE = "24";
    private static final int MAX_QUERY_LENGTH = 100;

    private final CatalogQueries queries;

    CatalogController(CatalogQueries queries) {
        this.queries = queries;
    }

    @GetMapping("/entries")
    @Operation(
            summary = "Browse the catalog",
            description = """
                    Lists entries sorted by name, one page at a time. To get the next page, pass the \
                    `nextCursor` of the response back as `cursor`, with the same filters. `nextCursor` \
                    is null on the last page.""")
    @ApiResponse(responseCode = "200", description = "One page of entries")
    CatalogPage browse(
            @Parameter(description = "Only entries of this product line")
            @RequestParam(required = false) ProductLine productLine,
            @Parameter(description = "Only entries of this series, exact name", example = "Super Smash Bros.")
            @RequestParam(required = false) String series,
            @Parameter(description = "Only entries of this franchise, exact name", example = "The Legend of Zelda")
            @RequestParam(required = false) String franchise,
            @Parameter(description = "Only entries of this physical form")
            @RequestParam(required = false) FigureType figureType,
            @Parameter(description = "Where the previous page ended, as returned in `nextCursor`")
            @RequestParam(required = false) String cursor,
            @Parameter(description = "Maximum number of entries in the page")
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Min(1) @Max(CatalogQueries.MAX_PAGE_SIZE) int limit) {
        return queries.browse(new CatalogFilter(productLine, series, franchise, figureType), cursor, limit);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search the catalog",
            description = """
                    Finds entries whose name, character, series or franchise contain every word of `q`, \
                    best match first. Typos are tolerated, and case and accents are ignored: `pikachuu` \
                    finds Pikachu, `pokemon` finds Pokémon. Words shorter than 3 characters are ignored, \
                    so a query made only of them returns no results. Results aren't paginated.""")
    @ApiResponse(responseCode = "200", description = "The best matches first")
    CatalogSearchResults search(
            @Parameter(description = "The words to look for", example = "pikachuu")
            @RequestParam @NotBlank @Size(max = MAX_QUERY_LENGTH) String q,
            @Parameter(description = "Only entries of this product line")
            @RequestParam(required = false) ProductLine productLine,
            @Parameter(description = "Only entries of this series, exact name")
            @RequestParam(required = false) String series,
            @Parameter(description = "Only entries of this franchise, exact name")
            @RequestParam(required = false) String franchise,
            @Parameter(description = "Only entries of this physical form")
            @RequestParam(required = false) FigureType figureType,
            @Parameter(description = "Maximum number of results")
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) @Min(1) @Max(CatalogQueries.MAX_PAGE_SIZE) int limit) {
        return queries.search(q, new CatalogFilter(productLine, series, franchise, figureType), limit);
    }

    @GetMapping("/entries/{id}")
    @Operation(summary = "Get one entry", description = "Returns one entry, with its release date in each region.")
    @ApiResponse(responseCode = "200", description = "The entry")
    @ApiResponse(
            responseCode = "404",
            description = "No entry with this identifier",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    CatalogEntryView entry(@Parameter(description = "The entry's identifier") @PathVariable UUID id) {
        return queries.findById(new CatalogEntryId(id))
                .orElseThrow(() -> new ErrorResponseException(
                        HttpStatus.NOT_FOUND,
                        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "No catalog entry with id " + id),
                        null));
    }

    @GetMapping("/series")
    @Operation(summary = "List the series", description = "Returns every series with its number of entries, sorted by name.")
    @ApiResponse(responseCode = "200", description = "The series")
    List<SeriesSummary> series(
            @Parameter(description = "Only count entries of this product line")
            @RequestParam(required = false) ProductLine productLine,
            @Parameter(description = "Only count entries of this physical form")
            @RequestParam(required = false) FigureType figureType) {
        return queries.series(new CatalogFilter(productLine, null, null, figureType));
    }

    @ExceptionHandler(InvalidCursorException.class)
    ProblemDetail invalidCursor(InvalidCursorException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "The cursor is invalid: restart from the first page, without a cursor.");
    }
}
