# Glossary

The ubiquitous language of Mintbox. The code uses these exact terms: if a word changes
here, it changes in the code too.

## Catalog

| Term | Definition |
|------|------------|
| **Product line** | A family of NFC figures from one manufacturer, such as amiibo or Skylanders. Every catalog entry belongs to exactly one product line. |
| **Catalog entry** | One product that exists in the real world, for example "Link (The Legend of Zelda series)". It is reference data: members don't create catalog entries, they pick them. |
| **Series** | The release line a catalog entry belongs to, for example "Super Smash Bros." for amiibo or "Spyro's Adventure" for Skylanders. Collection progress is computed per series. |
| **Franchise** | The game universe a character comes from, for example "The Legend of Zelda". A franchise spans several series, and a series can contain several franchises. |
| **Figure type** | The physical form of a catalog entry: figure, card, yarn figure, band... |
| **Region** | The market a box was released for: Europe, North America, Japan, Australia. The same catalog entry can have different release dates and packaging per region. |
| **Catalog source** | Where catalog entries come from for a product line: an external API or a versioned file. |

## Members and collections

| Term | Definition |
|------|------------|
| **Member** | A registered user. A member can be a **buyer** and a **seller** at the same time; these are roles in a transaction, not types of accounts. |
| **Collection** | The set of collection items a member owns. It is a view, not a stored object. |
| **Collection item** | One physical copy of a catalog entry owned by a member, with its condition. Owning two copies of the same figure means two collection items. |
| **Duplicate** | A collection item whose catalog entry the member owns more than once. Duplicates are the natural candidates for selling. |
| **Series progress** | The number of distinct catalog entries of a series a member owns, out of the total in that series ("34 / 57"). |
| **Wishlist entry** | A catalog entry a member wants, optionally with a maximum price and a minimum condition. |

## Marketplace

| Term | Definition |
|------|------------|
| **Listing** | A member's offer to sell, at a given price. It references one or more catalog entries through its listed items. |
| **Listed item** | One catalog entry inside a listing, with its condition and details (box, region, already scanned). A listing has exactly one listed item for now. |
| **Condition** | The state of a listed item: sealed, new with damaged box, opened, loose (no box), damaged. |
| **Delivery method** | How the item reaches the buyer: shipping or hand delivery. A listing offers at least one. |
| **Negotiation** | The price discussion between one buyer and the seller about one listing. It holds the successive offers. |
| **Offer** | A price proposed by one party in a negotiation. The other party can accept it, decline it or counter it. |
| **Counter-offer** | An offer that answers a pending offer with another price. The previous offer is then marked as countered. |
| **Agreed price** | The price of an accepted offer. The buyer can check out at this price for a limited time. |

## Orders and money

| Term | Definition |
|------|------------|
| **Checkout** | The act of starting to buy a listing. It reserves the listing and creates an order waiting for payment. |
| **Reservation** | The temporary lock on a listing while one buyer pays. Nobody else can buy a reserved listing. |
| **Order** | A purchase of one listing by one buyer, from checkout to completion. |
| **Item price** | The price the seller receives for the item: the listing price or the agreed price. |
| **Buyer protection fee** | The fee the buyer pays to the platform on each order. It is the platform's revenue. |
| **Order total** | Item price + shipping cost + buyer protection fee. What the buyer pays. |
| **Escrow** | The period during which the platform holds the buyer's payment, until the order is completed. |
| **Payout** | The transfer of the item price to the seller once the order is completed. |
| **Protection window** | The period after delivery during which the buyer can open a dispute. When it ends without a dispute, the order is completed. |
| **Dispute** | A buyer's claim that the item doesn't match the listing or never arrived. It ends with a full refund, a partial refund, or no refund. |
| **Shipment** | The parcel sent by the seller, with its label and tracking. |
| **Hand-over code** | A code the buyer gives the seller during a hand delivery. Entering it confirms the delivery. |
| **Review** | A rating and comment left by the buyer about the seller, or by the seller about the buyer, after a completed order. |
