import Link from "next/link";
import { ThemeToggle } from "@/components/theme-toggle";
import { Wordmark } from "@/components/wordmark";

/** Sections that exist in the roadmap but not yet in the app. */
const UPCOMING_SECTIONS = ["Collection", "Sell"];

export function SiteHeader() {
  return (
    <header className="border-b border-line">
      <div className="mx-auto flex h-20 max-w-7xl items-center justify-between gap-6 px-4 sm:px-8">
        <Link href="/" aria-label="Mintbox, home" className="rounded-md focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-mint">
          <Wordmark />
        </Link>

        <nav aria-label="Main" className="flex items-center gap-6">
          <ul className="hidden items-center gap-6 sm:flex">
            {UPCOMING_SECTIONS.map((section) => (
              <li key={section} className="flex items-center gap-2 text-sm font-semibold text-ink-muted">
                {section}
                <span className="rounded-full bg-sun px-2 py-0.5 text-[11px] font-bold tracking-wide text-on-sun uppercase">
                  Soon
                </span>
              </li>
            ))}
          </ul>
          <ThemeToggle />
        </nav>
      </div>
    </header>
  );
}
