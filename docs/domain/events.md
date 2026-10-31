# Domain events

A domain event records something that happened in the domain, named in the past tense. The
module that owns the aggregate publishes it; other modules react to it in their own
transaction.

## Rules

- **Events carry identifiers and the few values consumers need**, not whole aggregates. A
  consumer that needs more calls the publisher's public API.
- **Events are immutable records**, part of the publishing module's public API.
- **Delivery is guaranteed** by Spring Modulith's event publication registry
  (see [ADR 0002](../adr/0002-modular-monolith-with-spring-modulith.md)): an event
  published in a committed transaction is delivered, even after a crash.
- **Consumers are idempotent.** An event can be delivered more than once (after a crash and
  a retry, for example), so reacting twice must have the same effect as reacting once.
- **Events are never renamed or changed in an incompatible way.** If the meaning changes,
  I create a new event.

## Catalog

| Event | Payload | Consumers |
|-------|---------|-----------|
| `CatalogEntryAdded` | entry, product line | — |
| `CatalogEntryUpdated` | entry | `listing` (refreshes its copy of the catalog fields) |

## Account

| Event | Payload | Consumers |
|-------|---------|-----------|
| `MemberRegistered` | member | `notification` (welcome email) |
| `MemberSuspended` | member | `listing` (pauses the member's active listings) |
| `MemberDeleted` | member | `listing`, `collection`, `negotiation` (clean up personal data) |

## Listing

| Event | Payload | Consumers |
|-------|---------|-----------|
| `ListingPublished` | listing, seller, catalog entries, price | `notification` (wishlist alerts) |
| `ListingPriceDropped` | listing, old price, new price | `notification` (alerts for members who saved it) |
| `ListingReserved` | listing | — |
| `ListingReleased` | listing | — |
| `ListingSold` | listing | `negotiation` (closes the other negotiations) |
| `ListingWithdrawn` | listing | `negotiation` (closes negotiations) |
| `ListingRemoved` | listing, reason | `negotiation` (closes negotiations), `notification` (informs the seller) |
| `ListingExpired` | listing | `notification` (suggests renewing) |

## Negotiation

| Event | Payload | Consumers |
|-------|---------|-----------|
| `OfferMade` | negotiation, offer, amount, author | `messaging` (shows it in the conversation), `notification` |
| `OfferCountered` | negotiation, previous offer, new offer, amount | `messaging`, `notification` |
| `OfferAccepted` | negotiation, offer, agreed price, deadline | `messaging`, `notification` |
| `OfferDeclined` | negotiation, offer | `messaging`, `notification` |
| `OfferExpired` | negotiation, offer | `notification` |

## Ordering

| Event | Payload | Consumers |
|-------|---------|-----------|
| `OrderPlaced` | order, listing, buyer, seller, total | — |
| `OrderPaid` | order | `notification` (tells the seller to ship) |
| `OrderShipped` | order, tracking reference | `notification` |
| `OrderDelivered` | order, protection window end | `notification` |
| `OrderCompleted` | order, listing, buyer, seller, catalog entries | `collection` (moves the item to the buyer's collection), `review` (opens reviews), `notification` |
| `OrderCancelled` | order, reason | `notification` |
| `OrderDisputed` | order | `notification` |
| `OrderRefunded` | order, amount | `notification` |

## Payment

| Event | Payload | Consumers |
|-------|---------|-----------|
| `PaymentSucceeded` | payment, order | `ordering` (order → paid) |
| `PaymentFailed` | payment, order, reason | `ordering` (order → cancelled) |
| `PayoutReleased` | payout, seller, amount | `notification` |

Payment events come from the payment provider's webhooks. The provider can send the same
webhook several times, so `payment` stores the identifier of every webhook it processes
and ignores the ones it has already seen.

## Shipping

| Event | Payload | Consumers |
|-------|---------|-----------|
| `ShipmentInTransit` | shipment, order | `ordering` (order → shipped) |
| `ShipmentDelivered` | shipment, order | `ordering` (order → delivered) |

## Review

| Event | Payload | Consumers |
|-------|---------|-----------|
| `ReviewPublished` | review, author, subject, rating | `notification` |
