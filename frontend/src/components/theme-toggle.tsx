"use client";

import { useTheme } from "next-themes";
import { useSyncExternalStore } from "react";

const subscribeToNothing = () => () => {};

/** True in the browser, false while rendering on the server. */
function useIsClient() {
  return useSyncExternalStore(subscribeToNothing, () => true, () => false);
}

export function ThemeToggle() {
  const { resolvedTheme, setTheme } = useTheme();
  // The theme is only known in the browser: render a neutral button on the server, so that
  // the server and client markup match.
  const isClient = useIsClient();
  const isDark = isClient && resolvedTheme === "dark";
  const label = isDark ? "Switch to light theme" : "Switch to dark theme";

  return (
    <button
      type="button"
      onClick={() => setTheme(isDark ? "light" : "dark")}
      aria-label={label}
      title={label}
      className="grid size-11 place-items-center rounded-full border border-line bg-surface text-ink transition-colors hover:border-mint focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-mint"
    >
      <svg aria-hidden="true" viewBox="0 0 24 24" className="size-5" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
        {isDark ? (
          <>
            <circle cx="12" cy="12" r="4" />
            <path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4" />
          </>
        ) : (
          <path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5Z" />
        )}
      </svg>
    </button>
  );
}
