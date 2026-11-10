import type { ReactNode } from "react";

/** The shelf: two columns on phones, three on tablets, four on wider screens. */
export function FigureGrid({ children }: { children: ReactNode }) {
  return <ul className="grid grid-cols-2 gap-3 sm:grid-cols-3 sm:gap-5 lg:grid-cols-4 lg:gap-6">{children}</ul>;
}
