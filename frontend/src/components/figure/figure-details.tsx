import Link from "next/link";
import { notFound } from "next/navigation";
import type { ReactNode } from "react";
import { Notice } from "@/components/notice";
import { BlisterBubble, HangTab } from "@/components/catalog/blister-bubble";
import { apiUrl } from "@/lib/api/api-url";
import { getCatalogEntry } from "@/lib/api/catalog";
import type { CatalogEntryView } from "@/lib/api/types";
import { catalogHref, FIGURE_TYPE_NAMES } from "@/lib/catalog/search";
import { formatReleaseDate, releaseDatesOf, releasedSameDay } from "@/lib/catalog/regions";

/** Everything the catalog knows about one figure, and what the next milestones will add. */
export async function FigureDetails({ id }: { id: string }) {
  let entry: CatalogEntryView | null;
  try {
    entry = await getCatalogEntry(id);
  } catch (error) {
    console.warn(`Catalog entry unavailable (is the backend running at ${apiUrl}?):`, String(error));
    return (
      <Notice title="This figure can't be reached right now." action={{ href: `/catalog/${id}`, label: "Try again" }}>
        The server isn&apos;t answering. Try again in a moment.
      </Notice>
    );
  }
  if (!entry) {
    notFound();
  }

  const releases = releaseDatesOf(entry.releaseDates);

  return (
    <article className="grid gap-10 md:grid-cols-[minmax(0,5fr)_minmax(0,7fr)] md:gap-14">
      <div className="relative self-start rounded-card border border-line bg-surface p-4 pt-9 sm:p-5 sm:pt-10">
        <HangTab />
        {/* Up to 5/12 of a 1280px-wide page, the whole width on phones. */}
        <BlisterBubble src={entry.imageUrl} alt={entry.name} priority sizes="(min-width: 768px) 480px, 100vw" />
      </div>

      <div>
        <Link
          href={catalogHref({ series: entry.series })}
          className="text-xs font-bold tracking-[0.14em] text-mint-ink uppercase underline-offset-4 hover:underline"
        >
          {entry.series}
        </Link>
        <h1 className="mt-3 font-display text-4xl leading-tight font-semibold text-balance sm:text-5xl">{entry.name}</h1>
        <p className="mt-4">
          <Link
            href={catalogHref({ figureType: entry.figureType })}
            className="rounded-full bg-mint-soft px-3 py-1 text-sm font-bold text-mint-ink hover:underline"
          >
            {FIGURE_TYPE_NAMES[entry.figureType]}
          </Link>
        </p>

        <dl className="mt-8 grid grid-cols-1 gap-x-8 gap-y-5 border-t border-line pt-6 sm:grid-cols-2">
          <Fact label="Character">{entry.character}</Fact>
          <Fact label="Franchise">{entry.franchise}</Fact>
          <Fact label="Product line">amiibo</Fact>
        </dl>

        <section aria-labelledby="releases-heading" className="mt-10">
          <h2 id="releases-heading" className="font-display text-xl font-semibold">
            Release dates
          </h2>
          {releases.length === 0 ? (
            <p className="mt-3 text-ink-muted">No release date is known for this figure.</p>
          ) : (
            <ul className="mt-4 divide-y divide-line rounded-card border border-line bg-surface">
              {releases.map((release) => (
                <li key={release.region} className="flex items-center justify-between gap-4 px-4 py-3 text-sm">
                  <span className="font-semibold">{release.region}</span>
                  <span className="flex items-center gap-2 text-ink-muted">
                    {release.first && (
                      <span className="rounded-full bg-sun px-2 py-0.5 text-[11px] font-bold tracking-wide text-on-sun uppercase">
                        First
                      </span>
                    )}
                    <time dateTime={release.date}>{formatReleaseDate(release.date)}</time>
                  </span>
                </li>
              ))}
            </ul>
          )}
          {releasedSameDay(releases) && <p className="mt-2 text-sm text-ink-muted">Released the same day in every region.</p>}
        </section>

        <div className="mt-10 grid gap-4 sm:grid-cols-2">
          <ComingSoon title="In collections">See who owns this figure, and add it to your own shelf.</ComingSoon>
          <ComingSoon title="For sale">Copies listed by other collectors, with their condition and price.</ComingSoon>
        </div>
      </div>
    </article>
  );
}

function Fact({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <dt className="text-xs font-bold tracking-wide text-ink-muted uppercase">{label}</dt>
      <dd className="mt-1 font-semibold">{children}</dd>
    </div>
  );
}

/** A section of a later milestone: says what it will hold, without inventing any data. */
function ComingSoon({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="rounded-card border border-dashed border-line p-5">
      <h2 className="flex items-center gap-2 font-display text-lg font-semibold">
        {title}
        <span className="rounded-full bg-sun px-2 py-0.5 font-sans text-[11px] font-bold tracking-wide text-on-sun uppercase">
          Soon
        </span>
      </h2>
      <p className="mt-2 text-sm text-ink-muted">{children}</p>
    </section>
  );
}

/** Takes the place of {@link FigureDetails} while it loads, with the same layout. */
export function FigureDetailsSkeleton() {
  return (
    <div aria-hidden className="grid gap-10 md:grid-cols-[minmax(0,5fr)_minmax(0,7fr)] md:gap-14">
      <div className="rounded-card border border-line bg-surface p-4 pt-9 sm:p-5 sm:pt-10">
        <div className="aspect-[4/5] animate-pulse rounded-bubble bg-surface-sunken" />
      </div>
      <div>
        <div className="h-4 w-32 animate-pulse rounded bg-surface-sunken" />
        <div className="mt-4 h-12 w-3/4 animate-pulse rounded bg-surface-sunken" />
        <div className="mt-5 h-7 w-20 animate-pulse rounded-full bg-surface-sunken" />
        <div className="mt-8 h-32 animate-pulse rounded-card bg-surface-sunken" />
        <div className="mt-10 h-48 animate-pulse rounded-card bg-surface-sunken" />
      </div>
    </div>
  );
}
