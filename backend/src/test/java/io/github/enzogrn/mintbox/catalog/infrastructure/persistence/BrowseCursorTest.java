package io.github.enzogrn.mintbox.catalog.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.enzogrn.mintbox.catalog.application.InvalidCursorException;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BrowseCursorTest {

    @ParameterizedTest
    @ValueSource(strings = {"Link", "Link: The Hero", "Pokémon", "Mr. Game & Watch"})
    void decodes_what_it_encoded_whatever_the_name(String name) {
        BrowseCursor cursor = new BrowseCursor(name, UUID.randomUUID());

        assertThat(BrowseCursor.decode(cursor.encode())).isEqualTo(cursor);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "forged", "%%%", "bm90LWEtdXVpZDpMaW5r"})
    void rejects_cursors_it_did_not_produce(String cursor) {
        assertThatThrownBy(() -> BrowseCursor.decode(cursor)).isInstanceOf(InvalidCursorException.class);
    }
}
