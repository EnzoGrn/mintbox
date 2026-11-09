"use client";

import { ThemeProvider as NextThemesProvider } from "next-themes";

/**
 * Applies the light or dark theme by setting data-theme on <html>. It follows the system
 * preference until the visitor picks a theme, which is then remembered in local storage.
 */
export function ThemeProvider({ children }: { children: React.ReactNode }) {
  return (
    <NextThemesProvider attribute="data-theme" defaultTheme="system" enableSystem disableTransitionOnChange>
      {children}
    </NextThemesProvider>
  );
}
