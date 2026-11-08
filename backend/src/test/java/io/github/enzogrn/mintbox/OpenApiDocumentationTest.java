package io.github.enzogrn.mintbox;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.context.WebApplicationContext;

/** Checks that the OpenAPI document and Swagger UI are served, and describe the API. */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OpenApiDocumentationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvcTester mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcTester.from(context);
    }

    @Test
    void serves_an_openapi_document_describing_the_api() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.info.title").asString().isEqualTo("Mintbox API");
    }

    @Test
    void documents_every_catalog_endpoint() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .bodyJson()
                .extractingPath("$.paths").asMap()
                .containsKeys(
                        "/api/catalog/entries",
                        "/api/catalog/entries/{id}",
                        "/api/catalog/search",
                        "/api/catalog/series");
    }

    @Test
    void documents_the_validation_rules_of_parameters() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .bodyJson()
                .extractingPath("$.paths['/api/catalog/entries'].get.parameters[?(@.name == 'limit')].schema.maximum")
                .asArray()
                .containsExactly(100);
    }

    @Test
    void leaves_the_actuator_out_of_the_document() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .bodyJson()
                .extractingPath("$.paths").asMap()
                .doesNotContainKey("/actuator/health");
    }

    @Test
    void uses_the_project_version() {
        assertThat(mvc.get().uri("/v3/api-docs"))
                .bodyJson()
                .extractingPath("$.info.version").asString().doesNotContain("@");
    }

    @Test
    void serves_swagger_ui() {
        assertThat(mvc.get().uri("/swagger-ui/index.html")).hasStatusOk();
    }
}
