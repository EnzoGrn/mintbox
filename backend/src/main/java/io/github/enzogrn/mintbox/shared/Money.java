package io.github.enzogrn.mintbox.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * An amount of money in a given currency, stored as a whole number of minor units (cents for
 * EUR).
 *
 * <p>Using a {@code long} keeps arithmetic exact, and equality unambiguous: two amounts are
 * equal when they have the same number of minor units and the same currency. See
 * {@code docs/adr/0005-money-as-integer-minor-units.md}.
 *
 * <p>An amount can be negative, for example to express a difference. Rules such as "a price
 * can't be negative" belong to the domain objects that use {@code Money}, not to
 * {@code Money} itself.
 *
 * @param minorUnits the amount in minor units, for example {@code 1250} for 12.50 EUR
 * @param currency   the currency; only currencies with two decimal places are supported
 */
public record Money(long minorUnits, Currency currency) implements Comparable<Money> {

    private static final int SUPPORTED_FRACTION_DIGITS = 2;
    private static final long BASIS_POINTS_PER_UNIT = 10_000;

    public Money {
        Objects.requireNonNull(currency, "currency must not be null");
        if (currency.getDefaultFractionDigits() != SUPPORTED_FRACTION_DIGITS) {
            throw new IllegalArgumentException(
                    "Unsupported currency " + currency + ": only currencies with "
                            + SUPPORTED_FRACTION_DIGITS + " decimal places are supported");
        }
    }

    public static Money of(long minorUnits, Currency currency) {
        return new Money(minorUnits, currency);
    }

    public static Money of(long minorUnits, String currencyCode) {
        return new Money(minorUnits, Currency.getInstance(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(0, currency);
    }

    /**
     * Returns the sum of this amount and {@code other}.
     *
     * @throws IllegalArgumentException if the currencies differ
     * @throws ArithmeticException      if the result overflows
     */
    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(minorUnits, other.minorUnits), currency);
    }

    /**
     * Returns this amount minus {@code other}. The result can be negative.
     *
     * @throws IllegalArgumentException if the currencies differ
     * @throws ArithmeticException      if the result overflows
     */
    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.subtractExact(minorUnits, other.minorUnits), currency);
    }

    /**
     * Returns a percentage of this amount, expressed in basis points (1 basis point = 0.01%,
     * so 500 means 5%), rounded to the nearest minor unit. Halves are rounded away from zero:
     * 5% of 0.10 EUR is 0.005 EUR, which becomes 0.01 EUR.
     *
     * @throws IllegalArgumentException if {@code basisPoints} is negative
     */
    public Money percentage(int basisPoints) {
        if (basisPoints < 0) {
            throw new IllegalArgumentException("basisPoints must not be negative: " + basisPoints);
        }
        long result = BigDecimal.valueOf(minorUnits)
                .multiply(BigDecimal.valueOf(basisPoints))
                .divide(BigDecimal.valueOf(BASIS_POINTS_PER_UNIT), 0, RoundingMode.HALF_UP)
                .longValueExact();
        return new Money(result, currency);
    }

    public boolean isZero() {
        return minorUnits == 0;
    }

    public boolean isNegative() {
        return minorUnits < 0;
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    /**
     * Compares two amounts of the same currency.
     *
     * @throws IllegalArgumentException if the currencies differ, since amounts in different
     *                                  currencies can't be ordered without an exchange rate
     */
    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return Long.compare(minorUnits, other.minorUnits);
    }

    /** Returns the amount in major units, for example {@code 12.50} for 1250 minor units. */
    public BigDecimal toMajorUnits() {
        return BigDecimal.valueOf(minorUnits, SUPPORTED_FRACTION_DIGITS);
    }

    /** Returns a technical representation such as {@code 12.50 EUR}, meant for logs. */
    @Override
    public String toString() {
        return toMajorUnits().toPlainString() + " " + currency.getCurrencyCode();
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Currency mismatch: " + currency + " and " + other.currency);
        }
    }
}
