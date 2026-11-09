/** The Mintbox name set in the display font: "mint" in ink, "box" in mint, with a sun dot. */
export function Wordmark() {
  return (
    <span className="relative inline-block font-display text-[1.75rem] leading-none font-semibold tracking-tight">
      <span className="text-ink">mint</span>
      <span className="text-mint-ink">box</span>
      <span aria-hidden="true" className="absolute -top-1 -right-2.5 size-2 rounded-full bg-sun" />
    </span>
  );
}
