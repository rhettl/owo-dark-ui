package io.github.rhettl.owodarkui.playground.tabs;

import io.github.rhettl.owodarkui.playground.PlaygroundContext;
import io.github.rhettl.owodarkui.playground.PlaygroundTab;
import io.github.rhettl.owodarkui.widget.InventoryGrid;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Actual handled player slots over a reusable, deliberately small skin. */
public final class InventoryTab implements PlaygroundTab {
    private final PlaygroundContext context;

    public InventoryTab(PlaygroundContext context) {
        this.context = context;
    }

    @Override public String tabName() { return "Inventory"; }

    @Override
    public FlowLayout getContent() {
        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(5).horizontalAlignment(HorizontalAlignment.CENTER);
        content.child(Components.label(Component.literal("PLAYER INVENTORY")
            .withStyle(ChatFormatting.GOLD)).shadow(false));
        content.child(Components.label(Component.literal("16px items / 18px wells / 20px pitch")
            .withStyle(ChatFormatting.GRAY)).shadow(false));

        content.child(InventoryGrid.framed(context::slot));

        var hint = Components.label(Component.literal("Your real inventory + hotbar. Try matching items, stack counts, and empty slots.")
            .withStyle(ChatFormatting.GRAY)).shadow(false);
        hint.horizontalSizing(Sizing.fill(100));
        hint.horizontalTextAlignment(HorizontalAlignment.CENTER);
        content.child(hint);
        return content;
    }
}
