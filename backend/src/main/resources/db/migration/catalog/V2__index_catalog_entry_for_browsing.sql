-- Supports browsing the catalog sorted by name, with keyset pagination:
--   ORDER BY name, id ... WHERE (name, id) > (:afterName, :afterId)
-- See JdbcCatalogQueries. Filters on series or franchise don't get their own index yet:
-- with about a thousand entries, PostgreSQL reads the whole table faster than an index.
CREATE INDEX catalog_entry_name_id_idx ON catalog.catalog_entry (name, id);
