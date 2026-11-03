-- Catalog module: every figure that exists in the real world.
-- See docs/domain/aggregates.md#catalog-entry and docs/adr/0010-one-database-schema-per-module.md.

CREATE SCHEMA catalog;

CREATE TABLE catalog.catalog_entry (
    id               uuid          PRIMARY KEY,
    version          bigint        NOT NULL,
    product_line     varchar(32)   NOT NULL,
    source_reference varchar(64)   NOT NULL,
    name             varchar(200)  NOT NULL,
    character_name   varchar(200)  NOT NULL,
    series           varchar(200)  NOT NULL,
    franchise        varchar(200)  NOT NULL,
    figure_type      varchar(32)   NOT NULL,
    image_url        varchar(2048) NOT NULL,
    -- Attributes specific to one product line (docs/adr/0006-multi-product-line-catalog.md).
    attributes       jsonb         NOT NULL DEFAULT '{}',

    -- Rule CAT-1: a source reference is unique within a product line.
    CONSTRAINT catalog_entry_source_reference_uk UNIQUE (product_line, source_reference)
);

-- One row per region where the entry was released. No ON DELETE CASCADE: catalog entries
-- are never deleted (rule CAT-2).
CREATE TABLE catalog.catalog_entry_release_date (
    entry_id     uuid        NOT NULL REFERENCES catalog.catalog_entry (id),
    region       varchar(32) NOT NULL,
    release_date date        NOT NULL,

    PRIMARY KEY (entry_id, region)
);
