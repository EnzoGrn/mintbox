import { FigureImage } from "./figure-image";

/** The plastic bubble of a blister pack, around a figure's picture. */
export function BlisterBubble({
  src,
  alt,
  priority,
  sizes,
}: {
  src: string;
  alt: string;
  priority?: boolean;
  sizes?: string;
}) {
  return (
    <div className="relative aspect-[4/5] overflow-hidden rounded-bubble bg-surface-sunken">
      <FigureImage src={src} alt={alt} priority={priority} sizes={sizes} />
      {/* The shine of the plastic. */}
      <span
        aria-hidden
        className="pointer-events-none absolute inset-0 rounded-bubble bg-[linear-gradient(135deg,rgb(255_255_255/0.55),transparent_38%)] dark:bg-[linear-gradient(135deg,rgb(255_255_255/0.10),transparent_38%)]"
      />
    </div>
  );
}

/** The slot of the hang tab at the top of the pack, as on a peg-hook package. */
export function HangTab() {
  return (
    <span aria-hidden className="absolute top-2.5 left-1/2 h-2 w-9 -translate-x-1/2 rounded-full border border-line bg-canvas" />
  );
}
