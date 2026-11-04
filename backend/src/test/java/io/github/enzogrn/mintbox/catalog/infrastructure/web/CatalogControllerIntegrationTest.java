package io.github.enzogrn.mintbox.catalog.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.enzogrn.mintbox.TestcontainersConfiguration;
import io.github.enzogrn.mintbox.catalog.CatalogTestData;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntry;
import io.github.enzogrn.mintbox.catalog.domain.CatalogEntryRepository;
import io.github.enzogrn.mintbox.catalog.domain.FigureType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Calls the HTTP API through the whole application, down to a real database. MockMvc runs
 * the request in the test's thread, so it sees the test data and is rolled back with it.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class CatalogControllerIntegrationTest {

    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CatalogEntryRepository repository;

    @Autowired
    private EntityManager entityManager;

    private MockMvcTester mvc;
    private CatalogEntry link;

    @BeforeEach
    void setUp() {
        mvc = MockMvcTester.from(context);
        CatalogTestData data = new CatalogTestData(repository, entityManager);
        data.add("Mario", "Super Smash Bros.", "Super Mario", FigureType.FIGURE);
        link = data.add("Link", "Super Smash Bros.", "The Legend of Zelda", FigureType.FIGURE);
        data.add("Isabelle", "Animal Crossing", "Animal Crossing", FigureType.CARD);
        data.flush();
    }

    @Test
    void lists_entries_one_page_at_a_time() {
        assertThat(mvc.get().uri("/api/catalog/entries?limit=2"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items[*].name").asArray().containsExactly("Isabelle", "Link");

        assertThat(mvc.get().uri("/api/catalog/entries?limit=2"))
                .bodyJson()
                .extractingPath("$.nextCursor").isNotNull();
    }

    @Test
    void filters_entries() {
        assertThat(mvc.get().uri("/api/catalog/entries")
                .param("series", "Super Smash Bros.")
                .param("figureType", "FIGURE"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items[*].name").asArray().containsExactly("Link", "Mario");
    }

    @Test
    void returns_one_entry_with_its_release_dates() {
        assertThat(mvc.get().uri("/api/catalog/entries/{id}", link.id().value()))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.releaseDates.EUROPE").asString().isEqualTo("2014-11-28");
    }

    @Test
    void lists_series_with_their_size() {
        assertThat(mvc.get().uri("/api/catalog/series"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[?(@.name == 'Super Smash Bros.')].entryCount").asArray().containsExactly(2);
    }

    @Test
    void searches_entries_tolerating_typos() {
        assertThat(mvc.get().uri("/api/catalog/search").param("q", "isabel"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items[*].name").asArray().containsExactly("Isabelle");
    }

    @Test
    void answers_400_for_a_search_without_a_query() {
        assertThat(mvc.get().uri("/api/catalog/search"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
        assertThat(mvc.get().uri("/api/catalog/search").param("q", "   "))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void answers_400_for_a_search_query_that_is_too_long() {
        assertThat(mvc.get().uri("/api/catalog/search").param("q", "mario ".repeat(30)))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
    }

    @Test
    void answers_404_with_a_problem_detail_for_an_unknown_entry() {
        assertThat(mvc.get().uri("/api/catalog/entries/{id}", "01a1a1ea-4c37-7f0e-83db-de823a253139"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
    }

    @Test
    void answers_400_for_an_identifier_that_is_not_a_uuid() {
        assertThat(mvc.get().uri("/api/catalog/entries/not-a-uuid"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
    }

    @Test
    void answers_400_for_an_unknown_figure_type() {
        assertThat(mvc.get().uri("/api/catalog/entries?figureType=STATUE"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
    }

    @Test
    void answers_400_for_a_page_size_out_of_bounds() {
        assertThat(mvc.get().uri("/api/catalog/entries?limit=1000"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
        assertThat(mvc.get().uri("/api/catalog/entries?limit=0"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void answers_400_for_an_invalid_cursor() {
        assertThat(mvc.get().uri("/api/catalog/entries?cursor=forged"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentTypeCompatibleWith(PROBLEM_JSON);
    }
}
