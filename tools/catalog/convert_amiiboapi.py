#!/usr/bin/env python3
"""Converts AmiiboAPI's database file into Mintbox's catalog snapshot format.

AmiiboAPI (MIT license) stores each amiibo under its 16-digit hexadecimal identifier, which
encodes the character, the type, the series and the game series. This script decodes it with
the lookup tables of the same file, and writes one explicit entry per amiibo, sorted by
identifier so that diffs stay readable.

The original repository (N3evin/AmiiboAPI) was archived on 2025-12-31. The data is now
maintained in the community fork 8bitDream/AmiiboAPI, which serves amiiboapi.org.

Usage:
    curl -o amiibo-source.json https://raw.githubusercontent.com/8bitDream/AmiiboAPI/master/database/amiibo.json
    python3 tools/catalog/convert_amiiboapi.py amiibo-source.json \
        > backend/src/main/resources/catalog/amiibo.json

A scheduled workflow (.github/workflows/catalog-snapshot.yml) runs this every week and opens
a pull request when the catalog changed. See docs/adr/0013-refresh-the-amiibo-snapshot-from-the-maintained-fork.md.
"""

import json
import sys

SOURCE = ("AmiiboAPI database (https://github.com/8bitDream/AmiiboAPI, fork of "
          "https://github.com/N3evin/AmiiboAPI), MIT license, (c) 2017 Nevin Vu")
IMAGE_URL = "https://raw.githubusercontent.com/8bitDream/AmiiboAPI/master/images/icon_{head}-{tail}.png"

FIGURE_TYPES = {
    "Figure": "FIGURE",
    "Card": "CARD",
    "Yarn": "YARN",
    "Band": "BAND",
    "Block": "BLOCK",
}

REGIONS = {
    "eu": "EUROPE",
    "na": "NORTH_AMERICA",
    "jp": "JAPAN",
    "au": "AUSTRALIA",
}


def convert(database):
    entries = []
    for identifier, amiibo in database["amiibos"].items():
        # Identifier layout, as decoded by AmiiboAPI: 0x CCCC VV TT  NNNN SS 02
        #   CCCC: character (its first 3 digits: game series), TT: type, SS: amiibo series.
        hex_id = identifier.removeprefix("0x").lower()
        head, tail = hex_id[:8], hex_id[8:]

        entries.append({
            "sourceReference": hex_id,
            "name": amiibo["name"].strip(),
            "character": database["characters"]["0x" + hex_id[0:4]],
            "series": database["amiibo_series"]["0x" + hex_id[12:14]],
            "franchise": database["game_series"]["0x" + hex_id[0:3]],
            "figureType": FIGURE_TYPES[database["types"]["0x" + hex_id[6:8]]],
            "releaseDates": {
                REGIONS[region]: date
                for region, date in sorted(amiibo["release"].items())
                if date is not None
            },
            "imageUrl": IMAGE_URL.format(head=head, tail=tail),
        })

    entries.sort(key=lambda entry: entry["sourceReference"])
    return {"productLine": "AMIIBO", "source": SOURCE, "entries": entries}


def main():
    if len(sys.argv) != 2:
        sys.exit("Usage: convert_amiiboapi.py path/to/amiibo.json")
    with open(sys.argv[1], encoding="utf-8") as file:
        database = json.load(file)
    json.dump(convert(database), sys.stdout, indent=2, ensure_ascii=False)
    sys.stdout.write("\n")


if __name__ == "__main__":
    main()
