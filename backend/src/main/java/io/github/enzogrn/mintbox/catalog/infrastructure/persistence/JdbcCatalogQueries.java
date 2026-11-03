package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import io.github.enzogrn.mintbox.catalog.application.CatalogEntrySummary;
import io.github.enzogrn.mintbox.catalog.application.CatalogEntryView;
import io.github.enzogrn.mintbox.catalog.application.CatalogFilter;
import io.github.enzogrn.mintbox.catalog.application.CatalogPage;
import io.github.enzogrn.mintbox.catalog.application.CatalogQueries;
import io.github.enzogrn.mintbox.catalog.application.SeriesSummary;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.ProductLine;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements catalog queries with plain SQL, without going through JPA or the domain model.
 * See {@code docs/adr/0012-read-queries-bypass-the-domain-model.md}.
 */
@Component
@Transactional(readOnly = true)
class JdbcCatalogQueries implements CatalogQueries {

    private static final String SUMMARY_COLUMNS =
            "id, name, character_name, series, franchise, figure_type, image_url";

    private final JdbcClient jdbc;

    JdbcCatalogQueries(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Uses keyset pagination: each page starts right after the last entry of the previous
     * one, through the {@code (name, id)} index. Unlike {@code OFFSET}, the cost doesn't grow
     * with the page number, and entries added meanwhile don't shift the pages.
     */
    @Override
    public CatalogPage browse(CatalogFilter filter, String cursor, int limit) {
        if (limit < 1 || limit > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_PAGE_SIZE + ": " + limit);
        }
        Where where = Where.of(filter);
        if (cursor != null) {
            BrowseCursor after = BrowseCursor.decode(cursor);
            where.add("(name, id) > (:afterName, :afterId)");
            where.params.put("afterName", after.name());
            where.params.put("afterId", after.id());
        }
        where.params.put("limit", limit + 1);

        // One extra row tells whether there is a next page, without a COUNT query.
        List<CatalogEntrySummary> rows = jdbc
                .sql("SELECT " + SUMMARY_COLUMNS + " FROM catalog.catalog_entry" + where.sql()
                        + " ORDER BY name, id LIMIT :limit")
                .params(where.params)
                .query(JdbcCatalogQueries::toSummary)
                .list();

        if (rows.size() <= limit) {
            return new CatalogPage(rows, null);
        }
        List<CatalogEntrySummary> page = rows.subList(0, limit);
        CatalogEntrySummary last = page.getLast();
        return new CatalogPage(page, new BrowseCursor(last.name(), last.id()).encode());
    }

    @Override
    public Optional<CatalogEntryView> findById(CatalogEntryId id) {
        Map<Region, LocalDate> releaseDates = new HashMap<>();
        jdbc.sql("SELECT region, release_date FROM catalog.catalog_entry_release_date WHERE entry_id = :id")
                .param("id", id.value())
                .query((RowCallbackHandler) row -> releaseDates.put(
                        Region.valueOf(row.getString("region")),
                        row.getObject("release_date", LocalDate.class)));

        return jdbc.sql("""
                        SELECT id, product_line, name, character_name, series, franchise, figure_type, image_url
                        FROM catalog.catalog_entry
                        WHERE id = :id
                        """)
                .param("id", id.value())
                .query((row, rowNumber) -> new CatalogEntryView(
                        row.getObject("id", UUID.class),
                        ProductLine.valueOf(row.getString("product_line")),
                        row.getString("name"),
                        row.getString("character_name"),
                        row.getString("series"),
                        row.getString("franchise"),
                        FigureType.valueOf(row.getString("figure_type")),
                        releaseDates,
                        URI.create(row.getString("image_url"))))
                .optional();
    }

    @Override
    public List<SeriesSummary> series(CatalogFilter filter) {
        Where where = Where.of(filter);
        return jdbc
                .sql("SELECT series, count(*) AS entry_count FROM catalog.catalog_entry" + where.sql()
                        + " GROUP BY series ORDER BY series")
                .params(where.params)
                .query((row, rowNumber) -> new SeriesSummary(row.getString("series"), row.getInt("entry_count")))
                .list();
    }

    private static CatalogEntrySummary toSummary(ResultSet row, int rowNumber) throws SQLException {
        return new CatalogEntrySummary(
                row.getObject("id", UUID.class),
                row.getString("name"),
                row.getString("character_name"),
                row.getString("series"),
                row.getString("franchise"),
                FigureType.valueOf(row.getString("figure_type")),
                URI.create(row.getString("image_url")));
    }

    /**
     * Builds a {@code WHERE} clause from the criteria that are set. Values always go through
     * named parameters, never into the SQL string, so they can't inject SQL.
     */
    private static final class Where {

        private final List<String> conditions = new ArrayList<>();
        private final Map<String, Object> params = new HashMap<>();

        static Where of(CatalogFilter filter) {
            Where where = new Where();
            if (filter.productLine() != null) {
                where.add("product_line = :productLine");
                where.params.put("productLine", filter.productLine().name());
            }
            if (filter.series() != null) {
                where.add("series = :series");
                where.params.put("series", filter.series());
            }
            if (filter.franchise() != null) {
                where.add("franchise = :franchise");
                where.params.put("franchise", filter.franchise());
            }
            if (filter.figureType() != null) {
                where.add("figure_type = :figureType");
                where.params.put("figureType", filter.figureType().name());
            }
            return where;
        }

        void add(String condition) {
            conditions.add(condition);
        }

        String sql() {
            return conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
        }
    }
}
