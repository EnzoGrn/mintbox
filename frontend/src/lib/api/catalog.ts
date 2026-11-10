import { cacheLife } from "next/cache";
import { CATALOG_PAGE_SIZE, type CatalogSearch } from "@/lib/catalog/search";
import { api } from "./client";
import type { CatalogPage, FigureType, SeriesSummary } from "./types";

/** A server that doesn't answer within this delay is treated as down. */
export const API_TIMEOUT_MS = 3_000;

/*
 * The catalog only changes when its snapshot is refreshed, so the functions below cache their
 * results for an hour instead of calling the backend on every page view. Their arguments are
 * part of the cache key. A failed call throws, and errors are not cached: the next request
 * tries again.
 */

/** Every series with its number of entries, of one figure type if given. */
export async function getSeries(figureType?: FigureType): Promise<SeriesSummary[]> {
  "use cache";
  cacheLife("hours");

  const { data, error } = await api.GET("/api/catalog/series", {
    params: { query: { figureType } },
    signal: AbortSignal.timeout(API_TIMEOUT_MS),
  });
  if (!data) {
    throw new Error(`Couldn't load the catalog series: ${JSON.stringify(error)}`);
  }
  return data;
}

export type CatalogStats = {
  figureCount: number;
  seriesCount: number;
};

/** Counts the figures and series of the whole catalog. */
export async function getCatalogStats(): Promise<CatalogStats> {
  const series = await getSeries();
  return {
    figureCount: series.reduce((total, { entryCount }) => total + entryCount, 0),
    seriesCount: series.length,
  };
}

/**
 * One page of the catalog, sorted by name.
 *
 * A cursor the backend rejects (malformed, or from a link edited by hand) isn't an error of
 * the application: it is returned as such, for the page to offer starting over.
 */
export async function getCatalogPage(
  search: CatalogSearch,
): Promise<{ page: CatalogPage } | { invalidCursor: true }> {
  "use cache";
  cacheLife("hours");

  const { data, error, response } = await api.GET("/api/catalog/entries", {
    params: {
      query: {
        series: search.series,
        figureType: search.figureType,
        cursor: search.cursor,
        limit: CATALOG_PAGE_SIZE,
      },
    },
    signal: AbortSignal.timeout(API_TIMEOUT_MS),
  });
  if (data) {
    return { page: data };
  }
  if (response.status === 400 && search.cursor) {
    return { invalidCursor: true };
  }
  throw new Error(`Couldn't load the catalog: ${response.status} ${JSON.stringify(error)}`);
}
