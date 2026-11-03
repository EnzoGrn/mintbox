package io.github.enzogrn.mintbox.catalog.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The release date of a catalog entry in each region where it was released.
 *
 * <p>A region without a date means the entry wasn't released there, or that the date is
 * unknown.
 *
 * @param dates release dates per region; the map is copied and can't be modified
 */
public record ReleaseDates(Map<Region, LocalDate> dates) {

    private static final ReleaseDates NONE = new ReleaseDates(Map.of());

    public ReleaseDates {
        Objects.requireNonNull(dates, "dates must not be null");
        // Map.copyOf rejects null keys and values.
        dates = Map.copyOf(dates);
    }

    public static ReleaseDates none() {
        return NONE;
    }

    public Optional<LocalDate> in(Region region) {
        return Optional.ofNullable(dates.get(region));
    }

    /** Returns the first release date, whatever the region. */
    public Optional<LocalDate> earliest() {
        return dates.values().stream().min(LocalDate::compareTo);
    }
}
