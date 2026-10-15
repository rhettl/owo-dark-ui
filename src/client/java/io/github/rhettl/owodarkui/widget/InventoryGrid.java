package io.github.rhettl.owodarkui.widget;

import io.github.rhettl.owodarkui.theme.Themes;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;

/**
 * Player inventory compositions built from real handled slots.
 *
 * <p>{@code slots} maps a menu slot index to its owo component, normally
 * {@code BaseOwoHandledScreen::slotAsComponent}. Indices 0-26 are the main
 * inventory and 27-35 the hotbar, as laid out by
 * {@link io.github.rhettl.owodarkui.menu.InventoryOnlyMenu}.
 */
public final class InventoryGrid {
    private InventoryGrid() {}

    /** A framed, titled 204x126 inventory and hotbar. */
    public static FlowLayout framed(IntFunction<io.wispforest.owo.ui.core.Component> slots) {
        return create(slots, false, 204, 126);
    }

    /** The framed layout in vanilla's light-grey colors. */
    public static FlowLayout light(IntFunction<io.wispforest.owo.ui.core.Component> slots, int width, int height) {
        return create(slots, true, width, height);
    }

    /** Dark slots on the enclosing background, without an outer frame. */
    public static FlowLayout unframed(IntFunction<io.wispforest.owo.ui.core.Component> slots) {
        var inventory = framed(slots);
        inventory.surface(Surface.BLANK);
        return inventory;
    }

    /** Native-size slots grouped under a left-aligned heading, 178x95. */
    public static FlowLayout headed(IntFunction<io.wispforest.owo.ui.core.Component> slots) {
        var group = Containers.verticalFlow(Sizing.fixed(178), Sizing.fixed(95));
        var heading = Components.label(Component.literal("Inventory")
            .withStyle(style -> style.withColor(Themes.palette().textPrimary()))).shadow(false);
        heading.margins(Insets.bottom(4));
        group.child(heading);
        var grid = Containers.verticalFlow(Sizing.content(), Sizing.content());
        grid.gap(2);
        for (int row = 0; row < 3; row++) grid.child(slotRow(slots, row * 9, false));
        group.child(grid);
        var hotbar = slotRow(slots, 27, false);
        hotbar.margins(Insets.top(5));
        group.child(hotbar);
        return group;
    }

    private static FlowLayout create(IntFunction<io.wispforest.owo.ui.core.Component> slots,
                                     boolean light, int width, int height) {
        var frame = Frames.frame(Sizing.fixed(width), Sizing.fixed(height), Frames.Style.DARK_ROUNDED);
        if (light) frame.outer().surface((context, component) -> {
            int x = component.x(), y = component.y(), w = component.width(), h = component.height();
            context.fill(x, y, x + w, y + h, 0xFFC6C6C6);
            context.fill(x, y, x + w, y + 1, 0xFF373737);
            context.fill(x, y, x + 1, y + h, 0xFF373737);
            context.fill(x, y + h - 1, x + w, y + h, 0xFFFFFFFF);
            context.fill(x + w - 1, y, x + w, y + h, 0xFFFFFFFF);
        });
        frame.content().padding(Insets.of(6));
        frame.content().gap(6).horizontalAlignment(HorizontalAlignment.CENTER);
        frame.content().child(Components.label(Component.literal("Inventory")
            .withStyle(style -> style.withColor(light ? 0x404040 : Themes.palette().textPrimary()))).shadow(false));
        var inventory = Containers.verticalFlow(Sizing.content(), Sizing.content());
        inventory.gap(2);
        for (int row = 0; row < 3; row++) inventory.child(slotRow(slots, row * 9, light));
        frame.content().child(inventory);
        frame.content().child(slotRow(slots, 27, light));
        return frame.outer();
    }

    private static FlowLayout slotRow(IntFunction<io.wispforest.owo.ui.core.Component> slots,
                                      int firstMenuSlot, boolean light) {
        var row = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(2);
        for (int column = 0; column < 9; column++) {
            row.child(InventorySlots.slot(slots.apply(firstMenuSlot + column), light));
        }
        return row;
    }
}
