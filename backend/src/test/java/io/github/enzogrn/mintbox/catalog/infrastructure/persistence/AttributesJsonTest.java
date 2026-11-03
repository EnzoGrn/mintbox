package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class AttributesJsonTest {

    @Test
    void writes_attributes_sorted_by_name() {
        String json = AttributesJson.write(Map.of("variant", "Light Core", "element", "fire"));

        assertThat(json).isEqualTo("{\"element\":\"fire\",\"variant\":\"Light Core\"}");
    }

    @Test
    void reads_back_what_it_wrote() {
        Map<String, String> attributes = Map.of("element", "fire", "variant", "Light Core");

        assertThat(AttributesJson.read(AttributesJson.write(attributes))).isEqualTo(attributes);
    }

    @Test
    void handles_no_attributes() {
        assertThat(AttributesJson.write(Map.of())).isEqualTo("{}");
        assertThat(AttributesJson.read("{}")).isEmpty();
    }

    @Test
    void reads_json_as_formatted_by_postgresql() {
        // PostgreSQL normalizes jsonb, adding a space after colons.
        assertThat(AttributesJson.read("{\"element\": \"fire\"}")).isEqualTo(Map.of("element", "fire"));
    }
}
