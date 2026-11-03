# Catalog tools

## `convert_amiiboapi.py`

Converts the database file of [AmiiboAPI](https://github.com/N3evin/AmiiboAPI) (MIT license,
© 2017 Nevin Vu) into the snapshot format read by the backend.

```bash
curl -o amiibo-source.json https://raw.githubusercontent.com/N3evin/AmiiboAPI/master/database/amiibo.json
python3 tools/catalog/convert_amiiboapi.py amiibo-source.json > backend/src/main/resources/catalog/amiibo.json
```

The AmiiboAPI repository was archived on 2025-12-31, so this conversion was run once. The
snapshot is now maintained by hand: see
[ADR 0011](../../docs/adr/0011-amiibo-snapshot-as-source-of-truth.md).

## Snapshot format

```json
{
  "productLine": "AMIIBO",
  "source": "Where the data comes from",
  "entries": [
    {
      "sourceReference": "0100000000040002",
      "name": "Link",
      "character": "Link",
      "series": "Super Smash Bros.",
      "franchise": "The Legend of Zelda",
      "figureType": "FIGURE",
      "releaseDates": { "EUROPE": "2014-11-28", "NORTH_AMERICA": "2014-11-21" },
      "imageUrl": "https://example.com/link.png",
      "attributes": { }
    }
  ]
}
```

- `figureType`: `FIGURE`, `CARD`, `YARN`, `BAND` or `BLOCK`.
- `releaseDates`: optional; regions are `EUROPE`, `NORTH_AMERICA`, `JAPAN`, `AUSTRALIA`.
- `imageUrl`: must be an HTTPS URL.
- `attributes`: optional, only for product lines that declare line-specific attributes.

Keep entries sorted by `sourceReference`. To add an amiibo, add an entry with a new
16-digit hexadecimal `sourceReference` and run `./mvnw verify`: a test checks the whole file
against the domain rules.
