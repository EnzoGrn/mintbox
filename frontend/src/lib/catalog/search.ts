import type { FigureType } from "@/lib/api/types";

/**
 * What the catalog page shows, read from and written to its URL: a filtered view can be
 * shared, bookmarked and reloaded.
 *
 * Example: /catalog?type=card&series=Animal%20Crossing
 */
export type CatalogSearch = {
  series?: string;
  figureType?: FigureType;
  /** Where the page starts, when a "Load more" link is opened in a new tab. */
  cursor?: string;
};

/** Entries per page of the catalog. A multiple of 2, 3 and 4, the grid's column counts. */
export const CATALOG_PAGE_SIZE = 24;

type FigureTypeOption = {
  value: FigureType;
  /** How the type appears in the URL. */
  slug: string;
  /** The plural noun, used for filters and counts. */
  label: string;
};

/** Ordered by how many entries each type has, so the useful filters come first. */
export const FIGURE_TYPES: readonly FigureTypeOption[] = [
  { value: "FIGURE", slug: "figure", label: "Figures" },
  { value: "CARD", slug: "card", label: "Cards" },
  { value: "BAND", slug: "band", label: "Bands" },
  { value: "BLOCK", slug: "block", label: "Blocks" },
  { value: "YARN", slug: "yarn", label: "Yarn figures" },
];

/** The singular noun of a figure type, for a card's label. */
export const FIGURE_TYPE_NAMES: Record<FigureType, string> = {
  FIGURE: "Figure",
  CARD: "Card",
  BAND: "Band",
  BLOCK: "Block",
  YARN: "Yarn figure",
};

type SearchParams = Record<string, string | string[] | undefined>;

function single(value: string | string[] | undefined): string | undefined {
  const first = Array.isArray(value) ? value[0] : value;
  return first?.trim() || undefined;
}

/** Reads the search from URL parameters, ignoring empty and unknown values. */
export function parseCatalogSearch(params: SearchParams): CatalogSearch {
  const type = single(params.type);
  return {
    series: single(params.series),
    figureType: FIGURE_TYPES.find((option) => option.slug === type)?.value,
    cursor: single(params.cursor),
  };
}

/** The URL of the catalog page for a search. */
export function catalogHref({ series, figureType, cursor }: CatalogSearch): string {
  const params = new URLSearchParams();
  const type = FIGURE_TYPES.find((option) => option.value === figureType)?.slug;
  if (type) params.set("type", type);
  if (series) params.set("series", series);
  if (cursor) params.set("cursor", cursor);
  const query = params.toString();
  return query ? `/catalog?${query}` : "/catalog";
}
