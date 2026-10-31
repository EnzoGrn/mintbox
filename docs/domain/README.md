# Domain model

This folder describes Mintbox's business domain, independently of any framework or
database. I wrote it before the SQL schema and the code: the schema follows the model,
not the other way around.

## Reading order

1. [Glossary](glossary.md): the vocabulary used everywhere, from these documents to class
   names.
2. [Modules](modules.md): the business areas, what each one owns, and how they depend on
   each other.
3. [Aggregates](aggregates.md): the consistency boundaries, their rules and invariants.
4. [State machines](state-machines.md): the lifecycle of listings, negotiations and
   orders.
5. [Domain events](events.md): what happens in the domain, who publishes it and who
   reacts to it.

## Conventions

- Terms in **bold** in these documents are defined in the glossary, and the code uses
  exactly the same words.
- Durations and thresholds (reservation timeout, offer expiry...) are given as default
  values. In the code, they are configuration properties, not constants.
- Each rule that the code enforces has a stable identifier (for example `LST-3`), so that
  tests can reference it.
