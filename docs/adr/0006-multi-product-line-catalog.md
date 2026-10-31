# 0006. Multi-product-line catalog

- **Status:** Accepted
- **Date:** 2026-10-31

## Context

Mintbox starts with amiibo, but other lines of NFC figures (Skylanders, Disney Infinity,
Lego Dimensions...) share the same use: collectors track them, and buy and sell them second
hand. I want to be able to add a product line later without reworking the marketplace.

The lines have a lot in common (a name, a character, a series, a release date, an image),
but each has attributes of its own: a Skylander has an element and sometimes a variant
("Light Core", "Legendary"), an amiibo has a figure type (figure, card, yarn) and per-region
release dates.

Their sources also differ: amiibo have a community API, other lines may only be available
as a file I build myself.

## Decision

- Every catalog entry belongs to a **product line**. I implement only amiibo for now.
- **Shared attributes are regular columns** (`name`, `character`, `series`, `franchise`,
  `figure_type`, release dates, image URL), so that search, filters and indexes work the
  same way for every line.
- **Line-specific attributes go in a `jsonb` column**, validated in the code against a
  definition per product line. Unknown or badly typed attributes are rejected on import.
- **Each product line has its own catalog source**, behind a `CatalogSource` interface
  (a port, in hexagonal architecture terms). The amiibo implementation reads AmiiboAPI.
- **Nothing outside `catalog` knows about product lines.** Listings, negotiations, orders
  and collections reference a `CatalogEntryId`, whatever the line.

## Consequences

- Adding a product line means writing a `CatalogSource` implementation and an attribute
  definition. No other module changes.
- Filters on line-specific attributes (for example "fire element Skylanders") are less
  efficient than on regular columns. I can add a `jsonb` index for the few attributes that
  turn out to be searched often.
- The product line costs a column and an interface now, which is much cheaper than a
  migration of every table later.

## Alternatives considered

- **Amiibo only, generalize later:** simpler today, but the amiibo assumptions (the API
  identifiers, the figure types) would leak into listings and collections, and removing them
  would touch every module.
- **One table per product line:** strongly typed, but every query that spans lines (search,
  a member's collection) becomes a union, and every new line needs a migration and new
  queries.
- **Everything in `jsonb`:** maximum flexibility, but shared attributes would lose their
  types, their constraints and simple indexes, for no real benefit.
