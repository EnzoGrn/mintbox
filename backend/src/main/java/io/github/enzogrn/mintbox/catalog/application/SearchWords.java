package io.github.enzogrn.mintbox.catalog.application;

import java.util.Arrays;
import java.util.List;

/**
 * The words of a search query, as the search uses them.
 *
 * <p>Words shorter than {@value #MIN_WORD_LENGTH} characters are ignored: the search compares
 * groups of three letters, so a shorter word matches almost anything. Only the first
 * {@value #MAX_WORDS} words are kept, which bounds the cost of a query.
 *
 * @param values the words, in the order they were typed
 */
public record SearchWords(List<String> values) {

    public static final int MIN_WORD_LENGTH = 3;
    public static final int MAX_WORDS = 5;

    public SearchWords {
        values = List.copyOf(values);
    }

    /** Splits a query on spaces and punctuation: "Mr. Game & Watch" gives "game", "watch". */
    public static SearchWords parse(String query) {
        if (query == null) {
            return new SearchWords(List.of());
        }
        return new SearchWords(Arrays.stream(query.strip().split("[\\s\\p{Punct}]+"))
                .filter(word -> word.codePointCount(0, word.length()) >= MIN_WORD_LENGTH)
                .limit(MAX_WORDS)
                .toList());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
