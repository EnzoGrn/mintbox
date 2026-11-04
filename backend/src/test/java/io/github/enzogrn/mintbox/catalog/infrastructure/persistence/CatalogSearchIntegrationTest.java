package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.enzogrn.mintbox.TestcontainersConfiguration;
import io.github.enzogrn.mintbox.catalog.CatalogTestData;
import io.github.enzogrn.mintbox.catalog.application.CatalogEntrySummary;
import io.github.enzogrn.mintbox.catalog.application.CatalogFilter;
import io.github.enzogrn.mintbox.catalog.application.CatalogQueries;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/** Runs the search against a real PostgreSQL, with pg_trgm and unaccent from the V3 migration. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class CatalogSearchIntegrationTest {

    @Autowired
    private CatalogQueries queries;

    @Autowired
    private CatalogEntryRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void createCatalog() {
        CatalogTestData data = new CatalogTestData(repository, entityManager);
        data.add("Pikachu", "Super Smash Bros.", "Pokemon", FigureType.FIGURE);
        data.add("Detective Pikachu", "Pokemon", "Pokemon", FigureType.FIGURE);
        data.add("Pokémon Trainer", "Super Smash Bros.", "Pokemon", FigureType.FIGURE);
        data.add("Zelda", "Super Smash Bros.", "The Legend of Zelda", FigureType.FIGURE);
        data.add("Ganondorf", "Super Smash Bros.", "The Legend of Zelda", FigureType.FIGURE);
        data.add("Link", "Super Smash Bros.", "The Legend of Zelda", FigureType.FIGURE);
        data.add("Mario", "Super Smash Bros.", "Super Mario", FigureType.FIGURE);
        data.add("Baby Mario - Golf", "Mario Sports Superstars", "Super Mario", FigureType.CARD);
        data.add("Isabelle", "Animal Crossing", "Animal Crossing", FigureType.CARD);
        data.flush();
    }

    @Test
    void tolerates_a_typo() {
        assertThat(names("pikachuu")).containsExactly("Pikachu", "Detective Pikachu");
    }

    @Test
    void finds_a_word_from_its_beginning() {
        assertThat(names("isabel")).containsExactly("Isabelle");
    }

    @Test
    void ranks_matches_in_the_name_before_matches_in_the_series_or_franchise() {
        // All three belong to "The Legend of Zelda", but only one is named Zelda.
        assertThat(names("zeld")).first().isEqualTo("Zelda");
        assertThat(names("zeld")).containsExactlyInAnyOrder("Zelda", "Link", "Ganondorf");
    }

    @Test
    void ranks_the_shortest_name_first_between_equal_matches() {
        assertThat(names("mariio")).containsExactly("Mario", "Baby Mario - Golf");
    }

    @Test
    void ignores_accents_in_the_query_and_in_the_catalog() {
        assertThat(names("pokémon")).contains("Pokémon Trainer", "Pikachu");
        assertThat(names("pokemon trainer")).containsExactly("Pokémon Trainer");
    }

    @Test
    void requires_every_word_to_match() {
        assertThat(names("link zelda")).containsExactly("Link");
    }

    @Test
    void combines_the_search_with_filters() {
        List<CatalogEntrySummary> cards =
                queries.search("mario", new CatalogFilter(null, null, null, FigureType.CARD), 10).items();

        assertThat(cards).extracting(CatalogEntrySummary::name).containsExactly("Baby Mario - Golf");
    }

    @Test
    void returns_nothing_when_nothing_is_close() {
        assertThat(names("xyzzy")).isEmpty();
    }

    @Test
    void returns_nothing_for_a_query_without_a_usable_word() {
        assertThat(names("li")).isEmpty();
    }

    @Test
    void returns_at_most_the_requested_number_of_results() {
        assertThat(queries.search("smash", CatalogFilter.none(), 2).items()).hasSize(2);
    }

    private List<String> names(String query) {
        return queries.search(query, CatalogFilter.none(), 10).items().stream()
                .map(CatalogEntrySummary::name)
                .toList();
    }
}
