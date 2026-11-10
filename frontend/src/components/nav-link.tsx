"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";

/** A link of the main navigation, marked as current on its section's pages. */
export function NavLink({ href, children }: { href: string; children: ReactNode }) {
  const pathname = usePathname();
  const current = pathname === href || pathname.startsWith(`${href}/`);

  return (
    <Link
      href={href}
      aria-current={current ? "page" : undefined}
      className="relative rounded-md py-1 text-sm font-semibold text-ink-muted transition-colors hover:text-ink focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-mint aria-[current=page]:text-ink after:absolute after:inset-x-0 after:-bottom-1 after:h-0.5 after:rounded-full after:bg-mint after:opacity-0 aria-[current=page]:after:opacity-100"
    >
      {children}
    </Link>
  );
}
