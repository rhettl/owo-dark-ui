package io.github.rhettl.owodarkui.theme;

/**
 * Role-named ARGB colors used by every component. Components never hard-code a
 * color; they ask the active {@link Theme} for a role.
 *
 * <p>Start from {@link #DARK} and override only what you need:
 * <pre>{@code
 * Palette mine = Palette.DARK.toBuilder().accent(0xFF5AA0E0).accentHighlight(0xFF9CCBFF).build();
 * }</pre>
 */
public record Palette(
    /* Raised frame body and selected-tab body. */
    int frame,
    /* Hovered rows/tabs and interior panels. */
    int panel,
    /* Recessed well bodies and inactive tabs. */
    int well,
    /* Darkest bevel edge and outlines. */
    int edgeShadow,
    /* Mid bevel edge. */
    int edgeMid,
    /* Lightest bevel edge. */
    int edgeLight,
    /* Frame border tone where a selected tab joins its frame. */
    int border,
    /* Accent for selected rows and active tab top edge. */
    int accent,
    /* Accent for hovered or focused highlights. */
    int accentHighlight,
    int textPrimary,
    int textMuted,
    /* Persistent selection background. */
    int selected,
    int disabled
) {
    /** The original dark charcoal and brass look. */
    public static final Palette DARK = new Palette(
        brighten(0xFF1D211F),
        brighten(0xFF303330),
        brighten(0xFF151816),
        brighten(0xFF0D100F),
        brighten(0xFF414640),
        brighten(0xFF555B50),
        0xFF6B6949,
        0xFFC9AA56,
        0xFFF0D77A,
        0xFFF1E9D2,
        0xFFB7B09A,
        0xFF807A48,
        brighten(0xFF555853));

    public Builder toBuilder() {
        return new Builder(this);
    }

    /** Applies the two approved 10% RGB increases to a sampled dark tone, retaining alpha. */
    static int brighten(int color) {
        for (int pass = 0; pass < 2; pass++) {
            int red = Math.min(255, Math.round(((color >>> 16) & 255) * 1.10f));
            int green = Math.min(255, Math.round(((color >>> 8) & 255) * 1.10f));
            int blue = Math.min(255, Math.round((color & 255) * 1.10f));
            color = (color & 0xFF000000) | (red << 16) | (green << 8) | blue;
        }
        return color;
    }

    /** Copies a palette so individual roles can be replaced. */
    public static final class Builder {
        private int frame, panel, well, edgeShadow, edgeMid, edgeLight, border,
            accent, accentHighlight, textPrimary, textMuted, selected, disabled;

        private Builder(Palette base) {
            frame = base.frame;
            panel = base.panel;
            well = base.well;
            edgeShadow = base.edgeShadow;
            edgeMid = base.edgeMid;
            edgeLight = base.edgeLight;
            border = base.border;
            accent = base.accent;
            accentHighlight = base.accentHighlight;
            textPrimary = base.textPrimary;
            textMuted = base.textMuted;
            selected = base.selected;
            disabled = base.disabled;
        }

        public Builder frame(int color) { frame = color; return this; }
        public Builder panel(int color) { panel = color; return this; }
        public Builder well(int color) { well = color; return this; }
        public Builder edgeShadow(int color) { edgeShadow = color; return this; }
        public Builder edgeMid(int color) { edgeMid = color; return this; }
        public Builder edgeLight(int color) { edgeLight = color; return this; }
        public Builder border(int color) { border = color; return this; }
        public Builder accent(int color) { accent = color; return this; }
        public Builder accentHighlight(int color) { accentHighlight = color; return this; }
        public Builder textPrimary(int color) { textPrimary = color; return this; }
        public Builder textMuted(int color) { textMuted = color; return this; }
        public Builder selected(int color) { selected = color; return this; }
        public Builder disabled(int color) { disabled = color; return this; }

        public Palette build() {
            return new Palette(frame, panel, well, edgeShadow, edgeMid, edgeLight, border,
                accent, accentHighlight, textPrimary, textMuted, selected, disabled);
        }
    }
}
