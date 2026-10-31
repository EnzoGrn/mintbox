# 0005. Money as integer minor units

- **Status:** Accepted
- **Date:** 2026-10-30

## Context

Mintbox deals with prices, shipping costs, platform fees, full and partial refunds, and
seller payouts. A wrong amount is a bug that costs real money and trust.

I rule out floating-point types (`double`, `float`) from the start: they can't represent
most decimal amounts exactly (`0.1 + 0.2 != 0.3`). What I still need to decide is how to
represent amounts in the domain, the database and the API.

## Decision

- In the domain, I represent amounts with an immutable **`Money` value object**: a Java
  `record` holding the amount in **minor units as a `long`** (cents for EUR) and a
  `Currency`.
- `Money` protects its own rules: it rejects operations between two currencies, and prices
  and fees are validated as non-negative when they're created.
- When I split an amount (for example, taking a percentage as the platform fee), I apply an
  explicit, documented **rounding rule**, and tests check that the parts always add up to
  the total.
- In PostgreSQL, I store the amount as a `bigint` in minor units, next to a `char(3)`
  currency code.
- In the API, I send amounts as `{ "amount": 1250, "currency": "EUR" }` and let the
  frontend format them for display.
- Stripe also works in integer minor units, so nothing gets converted at that boundary.

## Consequences

- Arithmetic is exact and fast, and equality is unambiguous (`1250 == 1250`).
- The currency travels with the amount, so mixing currencies fails loudly instead of
  silently producing a wrong number.
- I only support currencies with two decimal places for now. Supporting other ones means
  reading the scale from `Currency`, which only touches `Money`.

## Alternatives considered

- **`BigDecimal`:** exact, but `equals` depends on the scale (`2.0` isn't equal to `2.00`),
  every operation needs an explicit rounding mode, and I'd have to convert at the Stripe
  boundary. Marketplace prices don't need arbitrary precision.
- **JSR 354 (Moneta):** a complete money API, but a heavy dependency for the few operations
  I need. A small value object of my own is also easier to read, and shows the domain
  modeling I want to demonstrate.
- **Plain `long` fields without a value object:** exact, but the currency and the rules
  would be lost, and nothing would stop me from adding a price to a quantity.
