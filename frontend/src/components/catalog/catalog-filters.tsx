import Link from "next/link";
import { getSeries } from "@/lib/api/catalog";
import { catalogHref, FIGURE_TYPES, type CatalogSearch } from "@/lib/catalog/search";
import { SeriesSelect } from "./series-select";

/**
 * Filters by figure type (links to each filtered view) and by series (a select).
 * If the series can't be loaded, the select is left out: the results say why.
 */
export async function CatalogFilters({ search }: { search: CatalogSearch }) {
  const series = await getSeries(search.figureType).catch(() => undefined);
  // A series in the URL with nothing of this type is still shown as selected.
  const options =
    series && search.series && !series.some((entry) => entry.name === search.series)
      ? [...series, { name: search.series, entryCount: 0 }]
      : series;

  return (
    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
      <TypeFilter search={search} />
      {options && <SeriesSelect search={search} options={options} />}
    </div>
  );
}

function TypeFilter({ search }: { search: CatalogSearch }) {
  const choices = [{ value: undefined, label: "All" }, ...FIGURE_TYPES];
  return (
    <nav aria-label="Figure type">
      {/* Scrolls sideways on narrow screens instead of wrapping onto several lines. */}
      <ul className="-mx-4 flex gap-2 overflow-x-auto px-4 pb-1 sm:mx-0 sm:px-0">
        {choices.map((choice) => {
          const current = choice.value === search.figureType;
          return (
            <li key={choice.label} className="shrink-0">
              <Link
                href={catalogHref({ series: search.series, figureType: choice.value })}
                aria-current={current ? "page" : undefined}
                className="inline-flex h-11 items-center rounded-full border border-line bg-surface px-4 text-sm font-semibold text-ink-muted transition-colors hover:border-mint hover:text-ink focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-mint aria-[current=page]:border-ink aria-[current=page]:bg-ink aria-[current=page]:text-canvas"
              >
                {choice.label}
              </Link>
            </li>
          );
        })}
      </ul>
    </nav>
  );
}

/** Takes the place of {@link CatalogFilters} while they load, at the same height. */
export function CatalogFiltersSkeleton() {
  return (
    <div aria-hidden className="flex flex-col gap-4 lg:flex-row lg:justify-between">
      <div className="flex gap-2">
        {Array.from({ length: 4 }, (_, index) => (
          <div key={index} className="h-11 w-20 animate-pulse rounded-full bg-surface-sunken" />
        ))}
      </div>
      <div className="h-11 w-64 animate-pulse rounded-full bg-surface-sunken" />
    </div>
  );
}
