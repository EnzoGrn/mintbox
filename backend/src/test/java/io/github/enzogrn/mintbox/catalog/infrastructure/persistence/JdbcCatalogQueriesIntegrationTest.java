package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.enzogrn.mintbox.TestcontainersConfiguration;
import io.github.enzogrn.mintbox.catalog.CatalogTestData;
import io.github.enzogrn.mintbox.catalog.application.CatalogEntrySummary;
import io.github.enzogrn.mintbox.catalog.application.CatalogFilter;
import io.github.enzogrn.mintbox.catalog.application.CatalogPage;
import io.github.enzogrn.mintbox.catalog.application.CatalogQueries;
import io.github.enzogrn.mintbox.catalog.application.InvalidCursorException;
import io.github.enzogrn.mintbox.catalog.application.SeriesSummary;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import io.github.enzogrn.mintbox.catalog.domain.Region;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class JdbcCatalogQueriesIntegrationTest {

    @Autowired
    private CatalogQueries queries;

    @Autowired
    private CatalogEntryRepository repository;

    @Autowired
    private EntityManager entityManager;

    private CatalogEntry linkSmash;

    @BeforeEach
    void createCatalog() {
        CatalogTestData data = new CatalogTestData(repository, entityManager);
        data.add("Mario", "Super Smash Bros.", "Super Mario", FigureType.FIGURE);
        linkSmash = data.add("Link", "Super Smash Bros.", "The Legend of Zelda", FigureType.FIGURE);
        data.add("Link", "The Legend of Zelda", "The Legend of Zelda", FigureType.FIGURE);
        data.add("Isabelle", "Animal Crossing", "Animal Crossing", FigureType.CARD);
        data.add("Zelda", "The Legend of Zelda", "The Legend of Zelda", FigureType.FIGURE);
        data.flush();
    }

    @Test
    void lists_entries_sorted_by_name() {
        CatalogPage page = queries.browse(CatalogFilter.none(), null, 10);

        assertThat(page.items()).extracting(CatalogEntrySummary::name)
                .containsExactly("Isabelle", "Link", "Link", "Mario", "Zelda");
        assertThat(page.nextCursor()).isNull();
    }

    @Test
    void goes_through_every_entry_page_by_page_without_duplicates() {
        List<CatalogEntrySummary> seen = new ArrayList<>();
        String cursor = null;
        int pages = 0;
        do {
            CatalogPage page = queries.browse(CatalogFilter.none(), cursor, 2);
            seen.addAll(page.items());
            cursor = page.nextCursor();
            pages++;
        } while (cursor != null);

        assertThat(pages).isEqualTo(3);
        assertThat(seen).extracting(CatalogEntrySummary::name)
                .containsExactly("Isabelle", "Link", "Link", "Mario", "Zelda");
        assertThat(seen).extracting(CatalogEntrySummary::id).doesNotHaveDuplicates();
    }

    @Test
    void filters_by_series() {
        CatalogPage page = queries.browse(new CatalogFilter(null, "The Legend of Zelda", null, null), null, 10);

        assertThat(page.items()).extracting(CatalogEntrySummary::name).containsExactly("Link", "Zelda");
    }

    @Test
    void combines_filters() {
        CatalogPage page = queries.browse(
                new CatalogFilter(null, null, "The Legend of Zelda", FigureType.FIGURE), null, 10);

        assertThat(page.items()).hasSize(3);
    }

    @Test
    void filters_by_figure_type() {
        CatalogPage page = queries.browse(new CatalogFilter(null, null, null, FigureType.CARD), null, 10);

        assertThat(page.items()).extracting(CatalogEntrySummary::name).containsExactly("Isabelle");
    }

    @Test
    void rejects_a_cursor_it_did_not_produce() {
        assertThatThrownBy(() -> queries.browse(CatalogFilter.none(), "not-a-cursor", 10))
                .isInstanceOf(InvalidCursorException.class);
    }

    @Test
    void returns_the_details_of_one_entry_with_its_release_dates() {
        assertThat(queries.findById(linkSmash.id())).hasValueSatisfying(view -> {
            assertThat(view.name()).isEqualTo("Link");
            assertThat(view.series()).isEqualTo("Super Smash Bros.");
            assertThat(view.releaseDates()).containsEntry(Region.EUROPE, LocalDate.of(2014, 11, 28));
        });
    }

    @Test
    void returns_nothing_for_an_unknown_entry() {
        assertThat(queries.findById(CatalogEntryId.generate())).isEmpty();
    }

    @Test
    void counts_entries_per_series() {
        assertThat(queries.series(CatalogFilter.none())).containsExactly(
                new SeriesSummary("Animal Crossing", 1),
                new SeriesSummary("Super Smash Bros.", 2),
                new SeriesSummary("The Legend of Zelda", 2));
    }
}
