-- Typo-tolerant search on catalog entries ("pikachuu", "zeld", "pokemon" for "Pokémon").
-- See JdbcCatalogQueries#search.

-- pg_trgm compares texts by their trigrams (groups of 3 letters), which makes it tolerant to
-- typos. unaccent removes accents. Both are "trusted" extensions: the database owner can
-- create them without being a superuser.
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS unaccent WITH SCHEMA public;

-- Lower case, without accents. unaccent() isn't declared IMMUTABLE, because its dictionary
-- could change, so it can't be used in a generated column or an index directly. Naming the
-- dictionary explicitly makes the result stable, which allows this wrapper to be IMMUTABLE.
-- The same function normalizes the search words, so both sides are compared alike.
CREATE FUNCTION catalog.normalize_search(value text) RETURNS text
    LANGUAGE sql
    IMMUTABLE PARALLEL SAFE STRICT
    RETURN lower(public.unaccent('public.unaccent'::regdictionary, value));

-- Everything a member may type to find an entry, kept up to date by PostgreSQL itself.
-- JPA never writes this column: it isn't mapped on the entity.
ALTER TABLE catalog.catalog_entry
    ADD COLUMN search_text text NOT NULL GENERATED ALWAYS AS (
        catalog.normalize_search(name || ' ' || character_name || ' ' || series || ' ' || franchise)
    ) STORED;

-- Serves the word similarity operator (<%) used by the search.
CREATE INDEX catalog_entry_search_text_trgm_idx
    ON catalog.catalog_entry USING gin (search_text public.gin_trgm_ops);
