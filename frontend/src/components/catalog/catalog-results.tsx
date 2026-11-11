import Link from "next/link";
import { getCatalogPage, getSeries } from "@/lib/api/catalog";
import { apiUrl } from "@/lib/api/api-url";
import { Notice } from "@/components/notice";
import { catalogHref, FIGURE_TYPES, type CatalogSearch } from "@/lib/catalog/search";
import { CatalogShelf } from "./catalog-shelf";
import { FigureCardSkeleton } from "./figure-card";
import { FigureGrid } from "./figure-grid";

const numberFormat = new Intl.NumberFormat("en-US");

/** The entries matching a search: the first page, then more on demand. */
export async function CatalogResults({ search }: { search: CatalogSearch }) {
  let result: Awaited<ReturnType<typeof getCatalogPage>>;
  let counts: TypeCount[];
  try {
    [result, counts] = await Promise.all([getCatalogPage(search), countByType(search)]);
  } catch (error) {
    console.warn(`Catalog unavailable (is the backend running at ${apiUrl}?):`, String(error));
    return (
      <Notice title="The catalog can't be reached right now." action={{ href: catalogHref(search), label: "Try again" }}>
        The figures are safe, the server just isn&apos;t answering. Try again in a moment.
      </Notice>
    );
  }

  const firstPage = { ...search, cursor: undefined };
  if ("invalidCursor" in result) {
    return (
      <Notice title="This page link isn't valid anymore." action={{ href: catalogHref(firstPage), label: "Start from the first page" }}>
        It may have been cut or edited. The first page has the same filters.
      </Notice>
    );
  }

  const label = (FIGURE_TYPES.find((type) => type.value === search.figureType)?.label ?? "Entries").toLowerCase();
  if (result.page.items.length === 0) {
    return (
      <Notice title={`No ${label} here.`} action={{ href: "/catalog", label: "Clear the filters" }}>
        {search.series ? `${search.series} has no ${label}.` : `The catalog has no ${label} yet.`}
      </Notice>
    );
  }

  return (
    <section aria-labelledby="results-heading">
      <div className="mb-5 flex flex-wrap items-baseline justify-between gap-x-4 gap-y-1">
        <h2 id="results-heading" className="text-sm text-ink-muted">
          <ResultCount counts={counts} />
        </h2>
        {search.cursor && (
          <Link href={catalogHref(firstPage)} className="text-sm font-semibold text-mint-ink underline-offset-4 hover:underline">
            Back to the first page
          </Link>
        )}
      </div>
      <CatalogShelf key={catalogHref(search)} search={search} firstPage={result.page} />
    </section>
  );
}

type TypeCount = { label: string; count: number };

/**
 * How many entries match the search, for each figure type that has some. The entries
 * endpoint has no total, so the counts come from the series, one call per type (cached).
 */
async function countByType(search: CatalogSearch): Promise<TypeCount[]> {
  const types = FIGURE_TYPES.filter((type) => search.figureType === undefined || type.value === search.figureType);
  const counts = await Promise.all(
    types.map(async (type) => {
      const series = await getSeries(type.value);
      const count = series
        .filter((entry) => search.series === undefined || entry.name === search.series)
        .reduce((total, entry) => total + entry.entryCount, 0);
      return { label: type.label.toLowerCase(), count };
    }),
  );
  return counts.filter(({ count }) => count > 0);
}

/** "258 figures", or the total split by type when several types match: "952 entries: 258 figures, 674 cards…" */
function ResultCount({ counts }: { counts: TypeCount[] }) {
  const total = counts.reduce((sum, { count }) => sum + count, 0);
  if (counts.length === 1) {
    return <Count {...counts[0]} />;
  }
  return (
    <>
      <Count count={total} label="entries" />:{" "}
      {counts.map((count, index) => (
        <span key={count.label}>
          {index > 0 && ", "}
          <Count {...count} />
        </span>
      ))}
    </>
  );
}

function Count({ count, label }: TypeCount) {
  // "figures" → "figure", "yarn figures" → "yarn figure", "entries" → "entry".
  const noun = count === 1 ? label.replace(/ies$/, "y").replace(/s$/, "") : label;
  return (
    <>
      <strong className="font-bold text-ink">{numberFormat.format(count)}</strong> {noun}
    </>
  );
}

/** Takes the place of {@link CatalogResults} while they load. */
export function CatalogResultsSkeleton() {
  return (
    <div>
      <div aria-hidden className="mb-5 h-5 w-28 animate-pulse rounded bg-surface-sunken" />
      <FigureGrid>
        {Array.from({ length: 8 }, (_, index) => (
          <li key={index}>
            <FigureCardSkeleton />
          </li>
        ))}
      </FigureGrid>
      <span className="sr-only">Loading the catalog…</span>
    </div>
  );
}
