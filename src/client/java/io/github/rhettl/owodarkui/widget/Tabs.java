package io.github.rhettl.owodarkui.widget;

import io.github.rhettl.owodarkui.theme.Palette;
import io.github.rhettl.owodarkui.theme.Themes;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** Focusable folder tabs and their connection to a frame. */
public final class Tabs {
    private Tabs() {}

    public enum State { ACTIVE, INACTIVE, DISABLED }

    /** A stateful text tab. The parent owns selection and supplies the click action. */
    public static ButtonComponent tab(String label, State state, Runnable onPress) {
        var tab = Components.button(Component.literal(label), button -> onPress.run());
        int width = Math.max(82, Minecraft.getInstance().font.width(label) + 24);
        tab.sizing(Sizing.fixed(width), Sizing.fixed(22));
        tab.textShadow(false);
        state(tab, state);
        return tab;
    }

    /** Update in place so selection changes retain keyboard focus. */
    public static void state(ButtonComponent tab, State state) {
        tab.active(state != State.DISABLED);
        var label = Component.literal(tab.getMessage().getString());
        tab.setMessage(state == State.DISABLED
            ? label.withStyle(ChatFormatting.GRAY, ChatFormatting.STRIKETHROUGH)
            : label.withStyle(state == State.ACTIVE ? ChatFormatting.GOLD : ChatFormatting.WHITE));
        tab.renderer((context, button, delta) -> {
            boolean hovered = button.isHoveredOrFocused();
            drawTab(context, button.getX(), button.getY(), button.getWidth(), button.getHeight(),
                state, hovered);
        });
    }

    /** Join the selected tab to this dark frame by opening only its top border. */
    public static void dock(Frames.Frame frame, Supplier<ButtonComponent> selectedTab) {
        Surface original = frame.outer().surface();
        frame.outer().surface((context, component) -> {
            original.draw(context, component);
            var tab = selectedTab.get();
            if (tab == null || tab.getY() + tab.getHeight() != component.y()) return;
            int left = Math.max(component.x() + 3, tab.getX());
            int right = Math.min(component.x() + component.width() - 3, tab.getX() + tab.getWidth());
            if (right - left < 4) return;
            var palette = Themes.palette();
            context.fill(left + 2, component.y(), right - 2, component.y() + 3, palette.frame());
            context.fill(left, component.y(), left + 1, component.y() + 3, palette.edgeShadow());
            context.fill(left + 1, component.y(), left + 2, component.y() + 3, palette.border());
            context.fill(right - 2, component.y(), right, component.y() + 3, palette.edgeShadow());
        });
    }

    private static void drawTab(OwoUIDrawContext context, int x, int y, int width, int height,
                                State state, boolean hovered) {
        Palette palette = Themes.palette();
        boolean selected = state == State.ACTIVE;
        boolean enabled = state != State.DISABLED;
        int top = y + (selected ? 0 : 3);
        int bottom = y + height;
        int body = selected ? palette.frame()
            : enabled && hovered ? palette.panel() : palette.well();
        int bevel = selected ? palette.border()
            : enabled && hovered ? palette.textMuted() : palette.edgeMid();
        int topBevel = selected
            ? hovered ? palette.accentHighlight() : palette.accent()
            : bevel;
        // Four-pixel rounded upper corners, drawn in logical pixels to stay crisp at every GUI scale.
        for (int row = 0; row < 4; row++) {
            int inset = switch (row) {
                case 0 -> 4;
                case 1 -> 2;
                case 2 -> 1;
                default -> 0;
            };
            context.fill(x + inset, top + row, x + width - inset, top + row + 1, palette.edgeShadow());
            if (row == 0) continue;
            context.fill(x + inset + 1, top + row, x + width - inset - 2, top + row + 1,
                row == 1 ? topBevel : body);
            if (row > 1) {
                context.fill(x + inset + 1, top + row, x + inset + 2, top + row + 1, bevel);
            }
        }
        context.fill(x, top + 4, x + width, bottom, palette.edgeShadow());
        context.fill(x + 1, top + 4, x + width - 2, bottom, body);
        context.fill(x + 1, top + 4, x + 2, bottom, bevel);
        if (!selected) context.fill(x + 2, bottom - 1, x + width - 2, bottom, palette.edgeShadow());
    }
}
