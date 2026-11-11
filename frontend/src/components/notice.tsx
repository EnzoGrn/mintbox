import Link from "next/link";
import type { ReactNode } from "react";

/**
 * A message in place of content that can't be shown (nothing found, server unreachable...),
 * with a way out.
 */
export function Notice({
  title,
  action,
  children,
  headingLevel = "p",
}: {
  title: string;
  action: { href: string; label: string };
  children: ReactNode;
  /** "h1" when the notice is the whole page, like a 404. */
  headingLevel?: "h1" | "p";
}) {
  const Heading = headingLevel;
  return (
    <div className="rounded-card border border-dashed border-line bg-surface px-6 py-14 text-center">
      <Heading className="font-display text-2xl font-semibold">{title}</Heading>
      <p className="mx-auto mt-2 max-w-md text-ink-muted">{children}</p>
      <Link
        href={action.href}
        className="mt-6 inline-flex h-11 items-center rounded-full border border-line bg-canvas px-5 text-sm font-bold transition-colors hover:border-mint focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-mint"
      >
        {action.label}
      </Link>
    </div>
  );
}
