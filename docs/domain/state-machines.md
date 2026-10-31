# State machines

Each diagram shows every allowed transition. Any transition that isn't drawn is refused by
the aggregate. Transitions triggered by time (expirations, timeouts) are run by scheduled
jobs, and their durations are configurable.

## Listing

```mermaid
stateDiagram-v2
    [*] --> DRAFT
    DRAFT --> ACTIVE: publish
    ACTIVE --> PAUSED: pause
    PAUSED --> ACTIVE: resume
    ACTIVE --> RESERVED: reserve (checkout)
    RESERVED --> ACTIVE: release (order cancelled)
    RESERVED --> SOLD: mark as sold (order paid)
    ACTIVE --> EXPIRED: after 90 days
    EXPIRED --> ACTIVE: renew
    DRAFT --> WITHDRAWN: withdraw
    ACTIVE --> WITHDRAWN: withdraw
    PAUSED --> WITHDRAWN: withdraw
    EXPIRED --> WITHDRAWN: withdraw
    ACTIVE --> REMOVED: remove (moderation)
    PAUSED --> REMOVED: remove (moderation)
    SOLD --> [*]
    WITHDRAWN --> [*]
    REMOVED --> [*]
```

- A **reserved** listing can't be paused, withdrawn or removed: an order is in progress,
  and the order decides what happens next.
- The original plan named the seller's removal `CANCELLED`. I renamed it **`WITHDRAWN`**,
  so that "cancelled" only means one thing in the whole domain: a cancelled order.
- **`REMOVED`** is reserved for moderation, so that a seller's own decision and a sanction
  stay distinguishable in the history and the statistics.

## Negotiation

A negotiation has its own lifecycle, and each offer inside it has another one.

```mermaid
stateDiagram-v2
    [*] --> OPEN: first offer
    OPEN --> AGREED: offer accepted
    AGREED --> OPEN: agreement lapses (24 hours)
    AGREED --> CONCLUDED: order paid at the agreed price
    OPEN --> CLOSED: listing sold, withdrawn or removed
    AGREED --> CLOSED: listing sold, withdrawn or removed
    CONCLUDED --> [*]
    CLOSED --> [*]
```

```mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> ACCEPTED: accept
    PENDING --> DECLINED: decline
    PENDING --> COUNTERED: counter (creates a new pending offer)
    PENDING --> WITHDRAWN: withdraw (author only)
    PENDING --> EXPIRED: after 48 hours
    ACCEPTED --> [*]
    DECLINED --> [*]
    COUNTERED --> [*]
    WITHDRAWN --> [*]
    EXPIRED --> [*]
```

- After an offer is declined, withdrawn or expired, the negotiation stays **open**: the
  buyer can make a new offer, within the limit of 5 offers.
- When a negotiation is closed, its pending offer, if any, expires with it.

## Order

```mermaid
stateDiagram-v2
    [*] --> PENDING_PAYMENT: checkout
    PENDING_PAYMENT --> PAID: payment succeeded
    PENDING_PAYMENT --> CANCELLED: payment failed, or after 15 minutes
    PAID --> SHIPPED: label scanned by the carrier
    PAID --> DELIVERED: hand-over code entered
    PAID --> CANCELLED: not shipped after 5 days (refunded)
    SHIPPED --> DELIVERED: carrier confirms delivery
    DELIVERED --> COMPLETED: protection window ends, or buyer confirms
    DELIVERED --> DISPUTED: buyer opens a dispute
    SHIPPED --> DISPUTED: parcel lost
    DISPUTED --> COMPLETED: resolved without refund, or with a partial refund
    DISPUTED --> REFUNDED: resolved with a full refund
    COMPLETED --> [*]
    CANCELLED --> [*]
    REFUNDED --> [*]
```

- **Side effects of each transition**, carried out by `ordering` through the other modules'
  APIs:

  | Transition | Effect |
  |------------|--------|
  | → `PENDING_PAYMENT` | The listing is reserved, a payment is created. |
  | → `PAID` | The listing is marked as sold, the negotiation (if any) is concluded. |
  | `PENDING_PAYMENT` → `CANCELLED` | The listing is released. |
  | `PAID` → `CANCELLED` | The buyer is fully refunded. The listing stays sold: the seller has to relist it. |
  | → `COMPLETED` | The payout to the seller is released, minus any partial refund. |
  | → `REFUNDED` | The buyer is fully refunded, no payout. |

- A partial refund doesn't need its own state: the order is completed, and the refund is
  recorded on it.
- A dispute is resolved by a moderator. Until the moderation module exists, disputes are
  resolved through an internal endpoint.
