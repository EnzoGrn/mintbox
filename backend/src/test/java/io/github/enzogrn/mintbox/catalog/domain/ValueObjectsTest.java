package io.github.enzogrn.mintbox.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValueObjectsTest {

    @Nested
    class ReleaseDatesBehavior {

        private final ReleaseDates dates = new ReleaseDates(Map.of(
                Region.EUROPE, LocalDate.of(2014, 11, 28),
                Region.JAPAN, LocalDate.of(2014, 12, 6),
                Region.NORTH_AMERICA, LocalDate.of(2014, 11, 21)));

        @Test
        void gives_the_date_for_a_region() {
            assertThat(dates.in(Region.EUROPE)).contains(LocalDate.of(2014, 11, 28));
        }

        @Test
        void gives_nothing_for_a_region_without_a_date() {
            assertThat(dates.in(Region.AUSTRALIA)).isEmpty();
        }

        @Test
        void gives_the_earliest_date_across_regions() {
            assertThat(dates.earliest()).contains(LocalDate.of(2014, 11, 21));
            assertThat(ReleaseDates.none().earliest()).isEmpty();
        }

        @Test
        void is_not_affected_by_later_changes_to_the_original_map() {
            Map<Region, LocalDate> original = new HashMap<>(Map.of(Region.EUROPE, LocalDate.of(2014, 11, 28)));
            ReleaseDates copy = new ReleaseDates(original);

            original.put(Region.JAPAN, LocalDate.of(2014, 12, 6));

            assertThat(copy.in(Region.JAPAN)).isEmpty();
        }
    }

    @Nested
    class SourceReferenceBehavior {

        @Test
        void removes_surrounding_spaces() {
            assertThat(new SourceReference(" 01000000034c0902 ").value()).isEqualTo("01000000034c0902");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        void rejects_a_blank_reference(String value) {
            assertThatThrownBy(() -> new SourceReference(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_a_reference_that_is_too_long() {
            assertThatThrownBy(() -> new SourceReference("x".repeat(SourceReference.MAX_LENGTH + 1)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class LineAttributesBehavior {

        @Test
        void rejects_blank_names_or_values() {
            assertThatThrownBy(() -> new LineAttributes(Map.of("element", " ")))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new LineAttributes(Map.of(" ", "fire")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void accepts_no_attribute_for_amiibo() {
            ProductLine.AMIIBO.validate(LineAttributes.none());
        }
    }
}
