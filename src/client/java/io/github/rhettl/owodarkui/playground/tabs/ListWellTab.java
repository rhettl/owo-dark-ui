package io.github.rhettl.owodarkui.playground.tabs;

import io.github.rhettl.owodarkui.playground.PlaygroundTab;
import io.github.rhettl.owodarkui.widget.ListWell;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Short and scrolling item/text lists with no list-item tooltips. */
public final class ListWellTab implements PlaygroundTab {
    private LabelComponent selection;

    @Override public String tabName() { return "List Well"; }

    @Override
    public FlowLayout getContent() {
        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(6);
        selection = Components.label(Component.literal("Select a row with click, Enter, or Space.")
            .withStyle(ChatFormatting.GRAY)).shadow(false);
        selection.horizontalSizing(Sizing.fill(100));
        var examples = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        examples.gap(8);

        var shortList = List.of(
            new ListWell.Entry(ItemStack.EMPTY, Component.literal("All items")),
            new ListWell.Entry(new ItemStack(Items.STONE), Component.literal("Materials")),
            new ListWell.Entry(new ItemStack(Items.IRON_PICKAXE), Component.literal("Tools")));
        examples.child(example("SHORT LIST", shortList, false));

        var items = List.of(Items.STONE, Items.OAK_LOG, Items.IRON_INGOT, Items.GOLD_INGOT,
            Items.EMERALD, Items.DIAMOND, Items.IRON_SWORD, Items.IRON_PICKAXE,
            Items.BREAD, Items.APPLE, Items.LEATHER, Items.BOOK);
        var entries = items.stream().map(ListWellTab::entry).toList();
        examples.child(example("SCROLLING LIST", entries, true));
        content.child(examples);
        content.child(selection);

        var hint = Components.label(Component.literal("Item icons and text, including a text-only row. Scroll inside the right well. No item tooltips.")
            .withStyle(ChatFormatting.GRAY)).shadow(false);
        hint.horizontalSizing(Sizing.fill(100));
        hint.horizontalTextAlignment(HorizontalAlignment.CENTER);
        content.child(hint);
        return content;
    }

    private FlowLayout example(String title, List<ListWell.Entry> entries, boolean scrollable) {
        var column = Containers.verticalFlow(Sizing.expand(50), Sizing.content());
        column.gap(5);
        column.child(Components.label(Component.literal(title).withStyle(ChatFormatting.GOLD)).shadow(false));
        column.child(ListWell.create(Sizing.fill(100), entries, 5, scrollable,
            entry -> selection.text(Component.literal("Selected: ").append(entry.text()))));
        return column;
    }

    private static ListWell.Entry entry(Item item) {
        var stack = new ItemStack(item);
        return new ListWell.Entry(stack, stack.getHoverName());
    }
}
