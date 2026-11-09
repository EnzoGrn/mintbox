import type { Metadata } from "next";
import localFont from "next/font/local";
import { SiteHeader } from "@/components/site-header";
import { ThemeProvider } from "@/components/theme-provider";
import "./globals.css";

/*
 * Fonts come from npm (Fontsource, SIL Open Font License) rather than Google Fonts: the build
 * needs no network access to Google, and visitors' browsers never contact Google either.
 * Both are variable fonts, so one file covers every weight.
 */
const fredoka = localFont({
  src: "../../node_modules/@fontsource-variable/fredoka/files/fredoka-latin-wght-normal.woff2",
  variable: "--font-fredoka",
  weight: "300 700",
  display: "swap",
});

const manrope = localFont({
  src: "../../node_modules/@fontsource-variable/manrope/files/manrope-latin-wght-normal.woff2",
  variable: "--font-manrope",
  weight: "200 800",
  display: "swap",
});

export const metadata: Metadata = {
  title: {
    default: "Mintbox",
    template: "%s · Mintbox",
  },
  description: "Track your collection of toys-to-life figures, and buy or sell them with other collectors.",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    // next-themes sets data-theme on <html> before React hydrates, hence the warning suppression.
    <html lang="en" className={`${fredoka.variable} ${manrope.variable}`} suppressHydrationWarning>
      <body className="min-h-dvh font-sans antialiased">
        <ThemeProvider>
          <SiteHeader />
          {children}
        </ThemeProvider>
      </body>
    </html>
  );
}
