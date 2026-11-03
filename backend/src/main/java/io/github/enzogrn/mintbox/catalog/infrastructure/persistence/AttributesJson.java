package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import java.util.Map;
import java.util.TreeMap;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/** Converts line-specific attributes to and from the JSON stored in the database. */
final class AttributesJson {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();
    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<>() {
    };

    private AttributesJson() {
    }

    /** Writes the attributes sorted by name, so that the same attributes give the same JSON. */
    static String write(Map<String, String> attributes) {
        return MAPPER.writeValueAsString(new TreeMap<>(attributes));
    }

    static Map<String, String> read(String json) {
        return MAPPER.readValue(json, MAP_TYPE);
    }
}
