import { cacheLife } from "next/cache";
import { api } from "./client";

/** A server that doesn't answer within this delay is treated as down. */
const TIMEOUT_MS = 3_000;

export type CatalogStats = {
  figureCount: number;
  seriesCount: number;
};

/**
 * Counts the figures and series of the catalog.
 *
 * The catalog only changes when its snapshot is refreshed, so the result is cached for an
 * hour instead of calling the backend on every page view. A failed call throws, and errors
 * are not cached: the next request tries again.
 */
export async function getCatalogStats(): Promise<CatalogStats> {
  "use cache";
  cacheLife("hours");

  const { data, error } = await api.GET("/api/catalog/series", {
    signal: AbortSignal.timeout(TIMEOUT_MS),
  });
  if (!data) {
    throw new Error(`Couldn't load the catalog series: ${JSON.stringify(error)}`);
  }

  return {
    figureCount: data.reduce((total, series) => total + (series.entryCount ?? 0), 0),
    seriesCount: data.length,
  };
}
