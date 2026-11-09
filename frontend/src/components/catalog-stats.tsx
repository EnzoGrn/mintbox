import { connection } from "next/server";
import { getCatalogStats, type CatalogStats as Stats } from "@/lib/api/catalog";
import { apiUrl } from "@/lib/api/api-url";

const numberFormat = new Intl.NumberFormat("en-US");

/**
 * The size of the catalog, read from the backend when the page is requested.
 *
 * Render it inside a Suspense boundary with {@link CatalogStatsSkeleton} as the fallback:
 * the rest of the page is prerendered and doesn't wait for the backend. If the backend is
 * down, the line is left out rather than breaking the page.
 */
export async function CatalogStats() {
  // Counted at request time, never at build time: the build doesn't need a running backend.
  await connection();

  let stats: Stats;
  try {
    stats = await getCatalogStats();
  } catch (error) {
    // Expected when the backend isn't running: one line is enough, not a stack trace.
    console.warn(`Catalog stats unavailable (is the backend running at ${apiUrl}?):`, String(error));
    return null;
  }

  return (
    <p className="inline-flex items-center gap-2.5 rounded-full border border-line bg-surface px-4 py-2 text-sm text-ink-muted">
      <span aria-hidden className="size-2 rounded-full bg-mint" />
      <span>
        <strong className="font-bold text-ink">{numberFormat.format(stats.figureCount)}</strong> figures across{" "}
        <strong className="font-bold text-ink">{numberFormat.format(stats.seriesCount)}</strong> series
      </span>
    </p>
  );
}

/** Takes the place of {@link CatalogStats} while it loads, at the same size. */
export function CatalogStatsSkeleton() {
  return (
    <p
      aria-hidden
      className="inline-flex h-[38px] w-64 animate-pulse rounded-full border border-line bg-surface-sunken"
    />
  );
}
