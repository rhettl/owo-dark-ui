package io.github.rhettl.owodarkui.theme;

import java.util.Objects;

/**
 * The active theme. Components read it while drawing, so a change applies to
 * open screens on the next frame. Colors a caller passes explicitly (for
 * example a well's body color) are fixed when the component is built.
 *
 * <p>Set it once during client initialization; it is shared by every mod that
 * uses this library in the same game instance.
 */
public final class Themes {
    private static volatile Theme current = Theme.DEFAULT;

    private Themes() {}

    public static Theme current() {
        return current;
    }

    /** Shorthand for {@code current().palette()}. */
    public static Palette palette() {
        return current.palette();
    }

    public static void set(Theme theme) {
        current = Objects.requireNonNull(theme, "theme");
    }

    public static void reset() {
        current = Theme.DEFAULT;
    }
}
