package io.github.rhettl.owodarkui.widget;

import io.github.rhettl.owodarkui.theme.Theme;
import io.github.rhettl.owodarkui.theme.Themes;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.NinePatchTexture;
import java.util.function.Function;
import java.util.function.IntSupplier;
import net.minecraft.resources.ResourceLocation;

/** Scalable frames, recessed wells, and panel surfaces. */
public final class Frames {
    private Frames() {}

    /** Outset-frame treatments for raised containers and their interaction states. */
    public enum Style {
        GOLD_ROUNDED(Theme.Textures::frameGoldRounded, 3, 2),
        DARK_ROUNDED(Theme.Textures::frameDarkRounded, 3, 2),
        GOLD_SQUARE(Theme.Textures::frameGoldSquare, 3, 0),
        DARK_SQUARE(Theme.Textures::frameDarkSquare, 3, 0);

        private final Function<Theme.Textures, ResourceLocation> texture;
        private final int edgeInset;
        private final int cornerRadius;

        Style(Function<Theme.Textures, ResourceLocation> texture, int edgeInset, int cornerRadius) {
            this.texture = texture;
            this.edgeInset = edgeInset;
            this.cornerRadius = cornerRadius;
        }

        /** The nine-patch this style draws with under the active theme. */
        public ResourceLocation texture() {
            return texture.apply(Themes.current().textures());
        }
    }

    /** A dark rounded frame whose body follows the theme's frame color. */
    public static Frame frame(Sizing horizontal, Sizing vertical) {
        return frame(horizontal, vertical, Style.DARK_ROUNDED);
    }

    /** A frame whose body follows the theme's frame color. */
    public static Frame frame(Sizing horizontal, Sizing vertical, Style style) {
        return frame(horizontal, vertical, style, () -> Themes.palette().frame());
    }

    /** A frame with a fixed body color. */
    public static Frame frame(Sizing horizontal, Sizing vertical, Style style, int bodyColor) {
        return frame(horizontal, vertical, style, () -> bodyColor);
    }

    private static Frame frame(Sizing horizontal, Sizing vertical, Style style, IntSupplier bodyColor) {
        var outer = Containers.verticalFlow(horizontal, vertical);
        outer.surface(frameSurface(style, bodyColor));
        outer.padding(Insets.of(style.edgeInset));

        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));
        content.padding(Insets.of(10));
        outer.child(content);
        return new Frame(outer, content);
    }

    /** A recessed well whose body follows the theme's well color. */
    public static Well well(Sizing horizontal, Sizing vertical) {
        return well(horizontal, vertical, () -> Themes.palette().well());
    }

    /** A recessed well with a fixed body color. */
    public static Well well(Sizing horizontal, Sizing vertical, int bodyColor) {
        return well(horizontal, vertical, () -> bodyColor);
    }

    private static Well well(Sizing horizontal, Sizing vertical, IntSupplier bodyColor) {
        var outer = Containers.verticalFlow(horizontal, vertical);
        outer.surface(wellSurface(bodyColor));
        outer.padding(Insets.of(4));

        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));
        content.padding(Insets.of(4));
        outer.child(content);
        return new Well(outer, content);
    }

    /** A dark interior panel intended to sit inside a frame. */
    public static FlowLayout panel(Sizing horizontal, Sizing vertical) {
        var panel = Containers.verticalFlow(horizontal, vertical);
        panel.surface((context, component) -> context.fill(component.x(), component.y(),
            component.x() + component.width(), component.y() + component.height(), Themes.palette().panel()));
        panel.padding(Insets.of(6));
        return panel;
    }

    private static Surface frameSurface(Style style, IntSupplier bodyColor) {
        return (context, component) -> {
            fillRoundedBody(context, component.x(), component.y(), component.width(), component.height(),
                style.cornerRadius, bodyColor.getAsInt());
            NinePatchTexture.draw(style.texture(), context, component);
        };
    }

    private static Surface wellSurface(IntSupplier bodyColor) {
        return (context, component) -> {
            context.fill(component.x(), component.y(), component.x() + component.width(),
                component.y() + component.height(), bodyColor.getAsInt());
            NinePatchTexture.draw(Themes.current().textures().well(), context, component);
        };
    }

    private static void fillRoundedBody(OwoUIDrawContext context, int x, int y, int width, int height,
                                        int radius, int color) {
        if (radius == 0) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }

        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + width, y + height - radius, color);
    }

    public record Frame(FlowLayout outer, FlowLayout content) {}

    public record Well(FlowLayout outer, FlowLayout content) {}
}
