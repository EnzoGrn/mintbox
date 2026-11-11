import type { Metadata } from "next";
import { Notice } from "@/components/notice";

export const metadata: Metadata = {
  title: "Not found",
};

/** Shown for any URL the app doesn't know, and for a figure that doesn't exist. */
export default function NotFound() {
  return (
    <main className="mx-auto max-w-7xl px-4 py-16 sm:px-8 sm:py-24">
      <Notice headingLevel="h1" title="Nothing on this shelf." action={{ href: "/catalog", label: "Browse the catalog" }}>
        This page doesn&apos;t exist, or the figure it showed isn&apos;t in the catalog.
      </Notice>
    </main>
  );
}
