package io.github.enzogrn.mintbox.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoneyTest {

    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency USD = Currency.getInstance("USD");

    @Nested
    class Creation {

        @Test
        void accepts_a_currency_with_two_decimal_places() {
            Money money = Money.of(1250, "EUR");

            assertThat(money.minorUnits()).isEqualTo(1250);
            assertThat(money.currency()).isEqualTo(EUR);
        }

        @Test
        void rejects_a_currency_without_decimal_places() {
            assertThatThrownBy(() -> Money.of(1250, "JPY"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("JPY");
        }

        @Test
        void rejects_a_currency_with_three_decimal_places() {
            assertThatThrownBy(() -> Money.of(1250, "KWD"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_a_missing_currency() {
            assertThatThrownBy(() -> Money.of(1250, (Currency) null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void creates_zero() {
            assertThat(Money.zero(EUR).isZero()).isTrue();
        }
    }

    @Nested
    class Equality {

        @Test
        void amounts_with_the_same_minor_units_and_currency_are_equal() {
            assertThat(Money.of(1250, EUR))
                    .isEqualTo(Money.of(1250, EUR))
                    .hasSameHashCodeAs(Money.of(1250, EUR));
        }

        @Test
        void amounts_in_different_currencies_are_not_equal() {
            assertThat(Money.of(1250, EUR)).isNotEqualTo(Money.of(1250, USD));
        }
    }

    @Nested
    class Arithmetic {

        @Test
        void adds_amounts_exactly() {
            // 0.10 + 0.20 is not exactly 0.30 with doubles, but it is with minor units.
            assertThat(Money.of(10, EUR).plus(Money.of(20, EUR))).isEqualTo(Money.of(30, EUR));
        }

        @Test
        void subtracts_amounts_and_can_go_below_zero() {
            Money result = Money.of(500, EUR).minus(Money.of(800, EUR));

            assertThat(result).isEqualTo(Money.of(-300, EUR));
            assertThat(result.isNegative()).isTrue();
        }

        @Test
        void refuses_to_add_amounts_in_different_currencies() {
            assertThatThrownBy(() -> Money.of(100, EUR).plus(Money.of(100, USD)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Currency mismatch");
        }

        @Test
        void refuses_to_subtract_amounts_in_different_currencies() {
            assertThatThrownBy(() -> Money.of(100, EUR).minus(Money.of(100, USD)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void fails_loudly_on_overflow_instead_of_wrapping_around() {
            assertThatThrownBy(() -> Money.of(Long.MAX_VALUE, EUR).plus(Money.of(1, EUR)))
                    .isInstanceOf(ArithmeticException.class);
        }
    }

    @Nested
    class Percentage {

        @ParameterizedTest(name = "{1} basis points of {0} = {2}")
        @CsvSource({
            "1000, 500,  50",   // 5% of 10.00 = 0.50
            "1999, 500, 100",   // 5% of 19.99 = 0.9995, rounded to 1.00
            "  10, 500,   1",   // 5% of 0.10 = 0.005, a half, rounded up to 0.01
            "   9, 500,   0",   // 5% of 0.09 = 0.0045, rounded down to 0.00
            "1000,   0,   0",
            "1000, 10000, 1000" // 100%
        })
        void rounds_to_the_nearest_minor_unit_with_halves_rounded_up(
                long amount, int basisPoints, long expected) {
            assertThat(Money.of(amount, EUR).percentage(basisPoints))
                    .isEqualTo(Money.of(expected, EUR));
        }

        @Test
        void rounds_halves_away_from_zero_for_negative_amounts() {
            assertThat(Money.of(-10, EUR).percentage(500)).isEqualTo(Money.of(-1, EUR));
        }

        @Test
        void rejects_a_negative_percentage() {
            assertThatThrownBy(() -> Money.of(1000, EUR).percentage(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void keeps_the_currency() {
            assertThat(Money.of(1000, USD).percentage(500).currency()).isEqualTo(USD);
        }
    }

    @Nested
    class Comparison {

        @Test
        void compares_amounts_of_the_same_currency() {
            Money small = Money.of(100, EUR);
            Money large = Money.of(200, EUR);

            assertThat(large.isGreaterThan(small)).isTrue();
            assertThat(small.isLessThan(large)).isTrue();
            assertThat(small.compareTo(Money.of(100, EUR))).isZero();
        }

        @Test
        void refuses_to_compare_amounts_in_different_currencies() {
            assertThatThrownBy(() -> Money.of(100, EUR).isGreaterThan(Money.of(100, USD)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Representation {

        @Test
        void converts_to_major_units() {
            assertThat(Money.of(1250, EUR).toMajorUnits()).isEqualTo(new BigDecimal("12.50"));
        }

        @Test
        void prints_a_technical_representation_for_logs() {
            assertThat(Money.of(1250, EUR)).hasToString("12.50 EUR");
            assertThat(Money.of(-5, EUR)).hasToString("-0.05 EUR");
        }
    }
}
