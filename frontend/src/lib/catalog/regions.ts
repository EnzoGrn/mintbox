/** The regions a figure can be released in, in the order the figure page lists them. */
export const REGIONS = [
  { key: "EUROPE", label: "Europe" },
  { key: "NORTH_AMERICA", label: "North America" },
  { key: "JAPAN", label: "Japan" },
  { key: "AUSTRALIA", label: "Australia" },
] as const;

const dateFormat = new Intl.DateTimeFormat("en-US", { dateStyle: "long", timeZone: "UTC" });

/** "2014-11-28" → "November 28, 2014". The date is a calendar day, not an instant: read in UTC. */
export function formatReleaseDate(isoDate: string): string {
  return dateFormat.format(new Date(`${isoDate}T00:00:00Z`));
}

export type ReleaseDate = { region: string; date: string; first: boolean };

/**
 * The known release dates, in region order. When the regions got the figure on different days,
 * the earliest release is flagged as the first; a worldwide release on one day flags none.
 */
export function releaseDatesOf(dates: Record<string, string>): ReleaseDate[] {
  const known = REGIONS.filter(({ key }) => dates[key]).map(({ key, label }) => ({ region: label, date: dates[key] }));
  const distinct = [...new Set(known.map(({ date }) => date))].sort();
  return known.map((release) => ({ ...release, first: distinct.length > 1 && release.date === distinct[0] }));
}

/** True when the figure came out on the same day in every region it was released in. */
export function releasedSameDay(releases: ReleaseDate[]): boolean {
  return releases.length > 1 && releases.every(({ date }) => date === releases[0].date);
}
