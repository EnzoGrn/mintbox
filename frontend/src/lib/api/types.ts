import type { components } from "./schema";

/** The API's models, under shorter names. */
type Schemas = components["schemas"];

export type CatalogEntrySummary = Schemas["CatalogEntrySummary"];
export type CatalogEntryView = Schemas["CatalogEntryView"];
export type CatalogPage = Schemas["CatalogPage"];
export type SeriesSummary = Schemas["SeriesSummary"];
export type FigureType = CatalogEntrySummary["figureType"];
