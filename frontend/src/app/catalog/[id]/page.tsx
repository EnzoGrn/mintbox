import type { Metadata } from "next";
import Link from "next/link";
import { Suspense } from "react";
import { FigureDetails, FigureDetailsSkeleton } from "@/components/figure/figure-details";
import { getCatalogEntry } from "@/lib/api/catalog";

/*
 * The 900-odd figure pages aren't generated at build time: that would need a running backend
 * during the build. The page shell is prerendered once, and each figure streams in when its
 * page is requested, cached like the rest of the catalog.
 */
export default function FigurePage({ params }: PageProps<"/catalog/[id]">) {
  return (
    <main className="mx-auto max-w-7xl px-4 pt-8 pb-20 sm:px-8 sm:pt-10">
      <Link
        href="/catalog"
        className="inline-flex items-center gap-1.5 rounded-md text-sm font-semibold text-ink-muted transition-colors hover:text-ink focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-mint"
      >
        <span aria-hidden>←</span> Catalog
      </Link>
      <div className="mt-6">
        <Suspense fallback={<FigureDetailsSkeleton />}>
          <Figure params={params} />
        </Suspense>
      </div>
    </main>
  );
}

async function Figure({ params }: Pick<PageProps<"/catalog/[id]">, "params">) {
  const { id } = await params;
  return <FigureDetails id={id} />;
}

/** The figure's name as the page title, for the browser tab and shared links. */
export async function generateMetadata({ params }: PageProps<"/catalog/[id]">): Promise<Metadata> {
  const { id } = await params;
  // Cached: the page itself reuses this response.
  const entry = await getCatalogEntry(id).catch(() => undefined);
  if (entry === null) {
    return { title: "Not found" };
  }
  if (entry === undefined) {
    // The backend is unreachable: the page says so.
    return { title: "Figure" };
  }
  return {
    title: entry.name,
    description: `${entry.name}, an amiibo of the ${entry.series} series (${entry.franchise}).`,
  };
}
