"use client";

import { useRouter } from "next/navigation";
import { useTransition } from "react";
import { catalogHref, type CatalogSearch } from "@/lib/catalog/search";

type Option = { name: string; entryCount: number };

/** Picks the series to show: the page follows the choice right away, with its URL. */
export function SeriesSelect({ search, options }: { search: CatalogSearch; options: Option[] }) {
  const router = useRouter();
  const [pending, startTransition] = useTransition();
  const total = options.reduce((sum, option) => sum + option.entryCount, 0);

  return (
    <div className="flex items-center gap-2">
      <label htmlFor="series" className="text-sm font-semibold text-ink-muted">
        Series
      </label>
      <select
        id="series"
        // A new search remounts the select with the series of the URL.
        key={catalogHref(search)}
        defaultValue={search.series ?? ""}
        aria-busy={pending}
        onChange={(event) => {
          const series = event.target.value || undefined;
          startTransition(() => router.push(catalogHref({ figureType: search.figureType, series })));
        }}
        className="h-11 max-w-[16rem] rounded-full border border-line bg-surface pr-9 pl-4 text-sm font-semibold text-ink transition-opacity hover:border-mint focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-mint aria-busy:opacity-60 sm:max-w-xs"
      >
        <option value="">All series ({total})</option>
        {options.map((option) => (
          <option key={option.name} value={option.name}>
            {option.name} ({option.entryCount})
          </option>
        ))}
      </select>
    </div>
  );
}
