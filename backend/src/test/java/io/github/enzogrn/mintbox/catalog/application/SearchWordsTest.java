package io.github.enzogrn.mintbox.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SearchWordsTest {

    @Test
    void splits_on_spaces_and_punctuation() {
        assertThat(SearchWords.parse("  link,zelda   botw ").values()).containsExactly("link", "zelda", "botw");
    }

    @Test
    void ignores_words_shorter_than_three_characters() {
        assertThat(SearchWords.parse("Mr. Game & Watch").values()).containsExactly("Game", "Watch");
    }

    @Test
    void counts_accented_letters_as_one_character() {
        assertThat(SearchWords.parse("Pé").isEmpty()).isTrue();
        assertThat(SearchWords.parse("Pém").values()).containsExactly("Pém");
    }

    @Test
    void keeps_only_the_first_words() {
        assertThat(SearchWords.parse("one two three four five six seven").values())
                .hasSize(SearchWords.MAX_WORDS)
                .startsWith("one")
                .endsWith("five");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "li", "a b c", "&&&"})
    void has_no_words_for_queries_without_a_usable_word(String query) {
        assertThat(SearchWords.parse(query).isEmpty()).isTrue();
    }
}
