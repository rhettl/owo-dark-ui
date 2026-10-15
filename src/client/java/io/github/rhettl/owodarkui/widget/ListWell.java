package io.github.rhettl.owodarkui.widget;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component.FocusSource;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Item-and-text rows in a recessed well, optionally using a fixed scroll viewport. */
public final class ListWell {
    private static final int ROW_HEIGHT = ListButton.HEIGHT;
    private static final int ROW_GAP = 2;
    private static final int BORDER_INSET = 3;

    private ListWell() {}

    /** An empty icon stack makes a text-only row. No automatic item tooltips. */
    public record Entry(ItemStack icon, Component text) {}

    public static FlowLayout create(Sizing width, List<Entry> entries, int visibleRows, boolean scrollable) {
        return create(width, entries, visibleRows, scrollable, entry -> {});
    }

    public static FlowLayout create(Sizing width, List<Entry> entries, int visibleRows, boolean scrollable,
                                    Consumer<Entry> onPress) {
        return create(width, entries, visibleRows, scrollable, 0, onPress);
    }

    /** Content padding is optional; the well's bevel is reserved separately. */
    public static FlowLayout create(Sizing width, List<Entry> entries, int visibleRows, boolean scrollable,
                                    int contentPadding, Consumer<Entry> onPress) {
        if (visibleRows < 1) throw new IllegalArgumentException("visibleRows must be positive");
        if (contentPadding < 0) throw new IllegalArgumentException("contentPadding must not be negative");
        int rowCount = scrollable ? visibleRows : Math.max(1, entries.size());
        int height = 2 * (BORDER_INSET + contentPadding)
            + rowCount * ROW_HEIGHT + (rowCount - 1) * ROW_GAP;
        var well = Frames.well(width, Sizing.fixed(height));
        // List rows meet the inner bevel by default, unlike the roomier general-purpose well.
        well.outer().padding(Insets.of(BORDER_INSET));
        well.content().padding(Insets.of(contentPadding));
        var list = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        list.gap(ROW_GAP);
        var buttons = new ArrayList<ListButton>();
        for (int index = 0; index < entries.size(); index++) {
            int selectedIndex = index;
            var button = new ListButton(entries.get(index), index, entry -> {
                for (int row = 0; row < buttons.size(); row++) {
                    buttons.get(row).selected(row == selectedIndex);
                }
                onPress.accept(entry);
            });
            buttons.add(button);
            list.child(button);
        }
        if (scrollable) {
            // Reserve the 3px track with just a 1px separator from the rows.
            list.padding(Insets.right(4));
            var scroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), list);
            scroll.scrollbarThiccness(3);
            // Tab navigation must reveal rows beyond the visible viewport.
            for (var child : list.children()) {
                child.focusGained().subscribe(source -> {
                    if (source == FocusSource.KEYBOARD_CYCLE
                        && (child.y() < scroll.y() || child.y() + child.height() > scroll.y() + scroll.height())) {
                        scroll.scrollTo(child);
                    }
                });
            }
            well.content().child(scroll);
        } else {
            well.content().child(list);
        }
        return well.outer();
    }
}
