package io.github.enzogrn.mintbox;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Keeps {@code docs/api/openapi.json}, the API contract the frontend's types are generated
 * from, identical to what the backend actually serves.
 *
 * <p>If the API changes, this test fails until the file is regenerated:
 *
 * <pre>./mvnw test -Dtest=OpenApiContractTest -Dmintbox.openapi.update=true</pre>
 *
 * <p>Committing the file makes every change of contract visible in pull request diffs.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OpenApiContractTest {

    /** Tests run from the backend folder; the contract lives in the repository's docs. */
    private static final Path CONTRACT = Path.of("..", "docs", "api", "openapi.json");
    private static final String UPDATE_PROPERTY = "mintbox.openapi.update";
    private static final String UPDATE_COMMAND =
            "./mvnw test -Dtest=OpenApiContractTest -D" + UPDATE_PROPERTY + "=true";

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    @Autowired
    private WebApplicationContext context;

    @Test
    void committed_contract_matches_the_api() throws IOException {
        JsonNode served = MAPPER.readTree(MockMvcTester.from(context).get().uri("/v3/api-docs").exchange()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8));

        if (Boolean.getBoolean(UPDATE_PROPERTY)) {
            Files.createDirectories(CONTRACT.getParent());
            Files.writeString(CONTRACT, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(served) + "\n");
            return;
        }

        assertThat(CONTRACT)
                .withFailMessage("%s doesn't exist. Generate it with: %s", CONTRACT, UPDATE_COMMAND)
                .exists();
        // Compared as JSON trees, so that formatting and line endings don't matter.
        assertThat(MAPPER.readTree(Files.readString(CONTRACT)))
                .withFailMessage("The API changed: update %s with: %s, then commit it.", CONTRACT, UPDATE_COMMAND)
                .isEqualTo(served);
    }
}
