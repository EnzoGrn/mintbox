# Catalog tools

## `convert_amiiboapi.py`

Converts the database file of AmiiboAPI (MIT license, © 2017 Nevin Vu) into the snapshot
format read by the backend. The data comes from
[8bitDream/AmiiboAPI](https://github.com/8bitDream/AmiiboAPI), the maintained fork that
serves [amiiboapi.org](https://amiiboapi.org/), since the original repository was archived.

```bash
curl -o amiibo-source.json https://raw.githubusercontent.com/8bitDream/AmiiboAPI/master/database/amiibo.json
python3 tools/catalog/convert_amiiboapi.py amiibo-source.json > backend/src/main/resources/catalog/amiibo.json
```

You normally don't need to run it: the
[Catalog snapshot](../../.github/workflows/catalog-snapshot.yml) workflow does it every
week and opens a pull request when the catalog changed. It can also be started by hand from
the Actions tab. See
[ADR 0013](../../docs/adr/0013-refresh-the-amiibo-snapshot-from-the-maintained-fork.md).

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

Entries are sorted by `sourceReference`. `./mvnw verify` checks the whole file against the
domain rules.
