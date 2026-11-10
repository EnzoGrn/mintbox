import type { Metadata } from "next";
import { Suspense } from "react";
import { CatalogFilters, CatalogFiltersSkeleton } from "@/components/catalog/catalog-filters";
import { CatalogResults, CatalogResultsSkeleton } from "@/components/catalog/catalog-results";
import { parseCatalogSearch } from "@/lib/catalog/search";

export const metadata: Metadata = {
  title: "Catalog",
  description: "Every amiibo, sorted by name, with filters by series and figure type.",
};

/*
 * The heading is prerendered at build time. The filters and results depend on the URL, so
 * they render when the page is requested and stream in behind their skeletons.
 */
export default function CatalogPage({ searchParams }: PageProps<"/catalog">) {
  return (
    <main className="mx-auto max-w-7xl px-4 pt-12 pb-20 sm:px-8 sm:pt-16">
      <p className="text-xs font-bold tracking-[0.14em] text-mint-ink uppercase">Catalog</p>
      <h1 className="mt-3 font-display text-4xl leading-tight font-semibold sm:text-5xl">Every figure, on one shelf.</h1>
      <p className="mt-3 max-w-xl text-ink-muted">
        Browse every amiibo ever released, by series or by type. More lines of figures will join them.
      </p>

      <div className="mt-10 mb-8">
        <Suspense fallback={<CatalogFiltersSkeleton />}>
          <Filters searchParams={searchParams} />
        </Suspense>
      </div>

      <Suspense fallback={<CatalogResultsSkeleton />}>
        <Results searchParams={searchParams} />
      </Suspense>
    </main>
  );
}

type SearchParamsProps = Pick<PageProps<"/catalog">, "searchParams">;

async function Filters({ searchParams }: SearchParamsProps) {
  return <CatalogFilters search={parseCatalogSearch(await searchParams)} />;
}

async function Results({ searchParams }: SearchParamsProps) {
  return <CatalogResults search={parseCatalogSearch(await searchParams)} />;
}
