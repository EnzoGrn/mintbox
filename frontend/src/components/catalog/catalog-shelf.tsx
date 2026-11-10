"use client";

import Link from "next/link";
import { useState, type MouseEvent } from "react";
import { api } from "@/lib/api/client";
import type { CatalogPage } from "@/lib/api/types";
import { CATALOG_PAGE_SIZE, catalogHref, type CatalogSearch } from "@/lib/catalog/search";
import { FigureCard } from "./figure-card";
import { FigureGrid } from "./figure-grid";

/** A server that doesn't answer within this delay is treated as down. */
const TIMEOUT_MS = 5_000;

/** How many cards of the first page load their image eagerly: the first rows on screen. */
const EAGER_IMAGES = 8;

type Status = "idle" | "loading" | "failed";

/**
 * The catalog's entries, with a "Load more" button that adds the next page below them.
 *
 * The button is a real link to the next page, so it can still be opened in a new tab. A click
 * adds the entries in place instead. Render the shelf with a `key` that changes with the
 * search, so that a new search starts over.
 */
export function CatalogShelf({ search, firstPage }: { search: CatalogSearch; firstPage: CatalogPage }) {
  const [items, setItems] = useState(firstPage.items);
  const [nextCursor, setNextCursor] = useState(firstPage.nextCursor);
  const [status, setStatus] = useState<Status>("idle");
  const [announcement, setAnnouncement] = useState("");

  async function loadMore(event: MouseEvent<HTMLAnchorElement>) {
    event.preventDefault();
    if (!nextCursor || status === "loading") {
      return;
    }
    setStatus("loading");
    try {
      // In the browser, the client calls /api on the page's origin (see next.config.ts).
      const { data } = await api.GET("/api/catalog/entries", {
        params: {
          query: {
            series: search.series,
            figureType: search.figureType,
            cursor: nextCursor,
            limit: CATALOG_PAGE_SIZE,
          },
        },
        signal: AbortSignal.timeout(TIMEOUT_MS),
      });
      if (!data) {
        throw new Error("The catalog didn't return a page");
      }
      setItems((current) => [...current, ...data.items]);
      setNextCursor(data.nextCursor);
      setStatus("idle");
      setAnnouncement(`${data.items.length} more loaded.`);
    } catch {
      setStatus("failed");
    }
  }

  return (
    <>
      <FigureGrid>
        {items.map((entry, index) => (
          <li key={entry.id}>
            <FigureCard entry={entry} priority={index < EAGER_IMAGES} />
          </li>
        ))}
      </FigureGrid>

      {/* Read out by screen readers when new entries are added. */}
      <p aria-live="polite" className="sr-only">
        {announcement}
      </p>

      {nextCursor && (
        <div className="mt-10 flex flex-col items-center gap-3">
          <Link
            href={catalogHref({ ...search, cursor: nextCursor })}
            prefetch={false}
            scroll={false}
            onClick={loadMore}
            aria-disabled={status === "loading"}
            className="inline-flex h-12 items-center rounded-full bg-mint px-7 font-bold text-on-mint transition hover:brightness-105 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-mint aria-disabled:cursor-progress aria-disabled:opacity-70"
          >
            {status === "loading" ? "Loading…" : "Load more"}
          </Link>
          {status === "failed" && (
            <p role="alert" className="text-sm text-ink-muted">
              The next page couldn&apos;t be loaded. Try again in a moment.
            </p>
          )}
        </div>
      )}
    </>
  );
}
