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
        Browse the figures across the worlds you collect. The catalog is on its way.
      </p>
      <div className="mt-8 min-h-[38px]">
        <Suspense fallback={<CatalogStatsSkeleton />}>
          <CatalogStats />
        </Suspense>
      </div>
    </main>
  );
}
