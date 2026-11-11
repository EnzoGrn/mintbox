"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Suspense, type ReactNode } from "react";

type NavLinkProps = { href: string; children: ReactNode };

/**
 * A link of the main navigation, marked as current on its section's pages.
 *
 * On pages whose URL is only known at request time (a figure's page), the current path can't
 * be read while prerendering: the link is prerendered unmarked, and marked once the path is
 * known.
 */
export function NavLink(props: NavLinkProps) {
  return (
    <Suspense fallback={<StyledLink {...props} current={false} />}>
      <CurrentAwareLink {...props} />
    </Suspense>
  );
}

function CurrentAwareLink(props: NavLinkProps) {
  const pathname = usePathname();
  const current = pathname === props.href || pathname.startsWith(`${props.href}/`);
  return <StyledLink {...props} current={current} />;
}

function StyledLink({ href, children, current }: NavLinkProps & { current: boolean }) {
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
