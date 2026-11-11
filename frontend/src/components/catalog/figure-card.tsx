import Link from "next/link";
import type { CatalogEntrySummary } from "@/lib/api/types";
import { FIGURE_TYPE_NAMES } from "@/lib/catalog/search";
import { BlisterBubble, HangTab } from "./blister-bubble";

/**
 * A catalog entry, drawn as a figure in its blister pack: a hang tab, a plastic bubble around
 * the figure, and the name on the card below.
 *
 * Catalog entries describe figures, not copies of them: condition and price belong to the
 * listings, so they never appear here. The whole card links to the figure's page.
 */
export function FigureCard({ entry, priority = false }: { entry: CatalogEntrySummary; priority?: boolean }) {
  return (
    <Link
      href={`/catalog/${entry.id}`}
      className="group block h-full rounded-card focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-mint"
    >
      <article className="relative flex h-full flex-col rounded-card border border-line bg-surface p-3 pt-7 shadow-[0_1px_0_var(--line)] transition duration-200 group-hover:-translate-y-1 group-hover:border-mint group-hover:shadow-[0_10px_24px_-12px_rgb(23_50_77/0.35)] motion-reduce:transition-none motion-reduce:group-hover:translate-y-0">
        <HangTab />
        <BlisterBubble src={entry.imageUrl} alt={entry.name} priority={priority} />

        <div className="flex flex-1 flex-col gap-1 px-1 pt-3">
          <h3 className="font-display text-base leading-snug font-semibold text-balance sm:text-lg">{entry.name}</h3>
          <p className="truncate text-sm text-ink-muted" title={entry.series}>
            {entry.series}
          </p>
          <p className="mt-auto pt-2">
            <span className="rounded-full bg-mint-soft px-2.5 py-0.5 text-xs font-bold text-mint-ink">
              {FIGURE_TYPE_NAMES[entry.figureType]}
            </span>
          </p>
        </div>
      </article>
    </Link>
  );
}

/** Takes the place of a {@link FigureCard} while it loads, at the same size. */
export function FigureCardSkeleton() {
  return (
    <div aria-hidden className="flex flex-col rounded-card border border-line bg-surface p-3 pt-7">
      <div className="aspect-[4/5] animate-pulse rounded-bubble bg-surface-sunken" />
      <div className="mt-3 h-5 w-3/4 animate-pulse rounded bg-surface-sunken" />
      <div className="mt-2 h-4 w-1/2 animate-pulse rounded bg-surface-sunken" />
      <div className="mt-4 h-5 w-14 animate-pulse rounded-full bg-surface-sunken" />
    </div>
  );
}
