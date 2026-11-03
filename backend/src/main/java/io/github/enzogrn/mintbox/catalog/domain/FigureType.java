package io.github.enzogrn.mintbox.catalog.domain;

/**
 * The physical form of a catalog entry: what you hold in your hand.
 *
 * <p>Forms are shared by every product line, so that members can filter by form across
 * lines. What an entry does in its game (a Skylanders trap or vehicle, an element, a
 * variant) is not a form: it belongs in the line's {@link LineAttributes}. A new product
 * line should only add the forms that don't exist yet.
 */
public enum FigureType {
    FIGURE,
    CARD,
    /** A plush figure made of yarn, such as the Yoshi's Woolly World amiibo. */
    YARN,
    /** A wristband with an NFC chip. */
    BAND,
    /** Wooden blocks, such as the "My Mario Wooden Blocks" amiibo. */
    BLOCK
}
