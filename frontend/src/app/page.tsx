import Link from "next/link";
import { Suspense } from "react";
import { CatalogStats, CatalogStatsSkeleton } from "@/components/catalog-stats";

export default function HomePage() {
  return (
    <main className="mx-auto max-w-7xl px-4 py-16 sm:px-8 sm:py-24">
      <p className="text-xs font-bold tracking-[0.14em] text-mint-ink uppercase">Your shelf, beautifully sorted</p>
      <h1 className="mt-4 max-w-xl font-display text-5xl leading-[1.05] font-semibold sm:text-6xl">
        Find your next <span className="text-mint-ink">favorite.</span>
      </h1>
      <p className="mt-5 max-w-md text-lg text-ink-muted">
        Browse the figures across the worlds you collect, and soon track yours and trade them.
      </p>
      <div className="mt-8 flex flex-wrap items-center gap-x-6 gap-y-4">
        <Link
          href="/catalog"
          className="inline-flex h-12 items-center gap-2 rounded-full bg-mint px-7 font-bold text-on-mint transition hover:brightness-105 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-mint"
      >
          Browse the catalog
          <span aria-hidden>→</span>
        </Link>
        <div className="min-h-[38px]">
          <Suspense fallback={<CatalogStatsSkeleton />}>
            <CatalogStats />
          </Suspense>
        </div>
      </div>
    </main>
  );
}
