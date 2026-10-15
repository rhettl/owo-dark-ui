package io.github.rhettl.owodarkui.widget;

import io.github.rhettl.owodarkui.theme.Themes;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;

/** Backdrops around real handled inventory slots. */
public final class InventorySlots {
    private InventorySlots() {}

    /** An 18px recessed backdrop around a native 16px handled inventory slot. */
    public static FlowLayout slot(Component slot) {
        return slot(slot, false);
    }

    /** {@code light} uses vanilla's light-grey slot colors instead of the theme. */
    public static FlowLayout slot(Component slot, boolean light) {
        var cell = Containers.verticalFlow(Sizing.fixed(18), Sizing.fixed(18));
        cell.padding(Insets.of(1));
        cell.surface((context, component) -> {
            var palette = Themes.palette();
            int x = component.x(), y = component.y();
            context.fill(x, y, x + 18, y + 18, light ? 0xFF8B8B8B : palette.well());
            context.fill(x, y, x + 18, y + 1, light ? 0xFF373737 : palette.edgeShadow());
            context.fill(x, y + 1, x + 1, y + 18, light ? 0xFF373737 : palette.edgeShadow());
            context.fill(x + 1, y + 17, x + 18, y + 18, light ? 0xFFFFFFFF : palette.edgeLight());
            context.fill(x + 17, y + 1, x + 18, y + 17, light ? 0xFFFFFFFF : palette.edgeMid());
        });
        slot.sizing(Sizing.fixed(16), Sizing.fixed(16));
        cell.child(slot);
        return cell;
    }
}
