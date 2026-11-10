import type { CatalogEntrySummary } from "@/lib/api/types";
import { FIGURE_TYPE_NAMES } from "@/lib/catalog/search";
import { FigureImage } from "./figure-image";

/**
 * A catalog entry, drawn as a figure in its blister pack: a hang tab, a plastic bubble around
 * the figure, and the name on the card below.
 *
 * Catalog entries describe figures, not copies of them: condition and price belong to the
 * listings, so they never appear here.
 */
export function FigureCard({ entry, priority = false }: { entry: CatalogEntrySummary; priority?: boolean }) {
  return (
    <article className="relative flex h-full flex-col rounded-card border border-line bg-surface p-3 pt-7 shadow-[0_1px_0_var(--line)]">
      {/* The hang tab's slot, as on a peg-hook package. */}
      <span aria-hidden className="absolute top-2.5 left-1/2 h-2 w-9 -translate-x-1/2 rounded-full border border-line bg-canvas" />

      <div className="relative aspect-[4/5] overflow-hidden rounded-bubble bg-surface-sunken">
        <FigureImage src={entry.imageUrl} alt={entry.name} priority={priority} />
        {/* The shine of the plastic bubble. */}
        <span
          aria-hidden
          className="pointer-events-none absolute inset-0 rounded-bubble bg-[linear-gradient(135deg,rgb(255_255_255/0.55),transparent_38%)] dark:bg-[linear-gradient(135deg,rgb(255_255_255/0.10),transparent_38%)]"
        />
      </div>

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
