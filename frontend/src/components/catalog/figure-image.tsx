"use client";

import Image from "next/image";
import { useState } from "react";

/**
 * A figure's picture, or a neutral silhouette when the picture can't be loaded: a missing
 * image shouldn't leave a broken icon in the middle of a shelf.
 */
export function FigureImage({
  src,
  alt,
  priority = false,
  // Card width in the catalog grid: two columns on phones, three on tablets, four from 1024px
  // in a 1280px-wide page.
  sizes = "(min-width: 1024px) 280px, (min-width: 640px) 30vw, 45vw",
}: {
  src: string;
  alt: string;
  priority?: boolean;
  /** How wide the image is displayed, for the browser to pick the right resolution. */
  sizes?: string;
}) {
  const [failed, setFailed] = useState(false);

  if (failed) {
    return (
      <svg role="img" aria-label={alt} viewBox="0 0 64 80" className="size-full p-[22%] text-ink-muted/35">
        <circle cx="32" cy="20" r="11" fill="currentColor" />
        <path d="M14 70c0-14 8-30 18-30s18 16 18 30z" fill="currentColor" />
        <ellipse cx="32" cy="72" rx="22" ry="4" fill="currentColor" />
      </svg>
    );
  }

  return (
    <Image
      src={src}
      alt={alt}
      fill
      sizes={sizes}
      priority={priority}
      onError={() => setFailed(true)}
      className="object-contain p-[12%] drop-shadow-[0_6px_8px_rgb(0_0_0/0.18)]"
    />
  );
}
