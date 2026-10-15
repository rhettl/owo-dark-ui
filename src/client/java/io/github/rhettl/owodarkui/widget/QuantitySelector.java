package io.github.rhettl.owodarkui.widget;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.function.IntConsumer;
import net.minecraft.network.chat.Component;

/** Quantity readout and bounded step controls; the caller owns the quantity. */
public final class QuantitySelector {
    private final FlowLayout outer;
    private final LabelComponent value;
    private final ButtonComponent up, down;
    private final boolean compact;

    public QuantitySelector(IntConsumer step) {
        this(step, false);
    }

    public QuantitySelector(IntConsumer step, boolean compact) {
        this.compact = compact;
        outer = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        outer.gap(compact ? 3 : 6).verticalAlignment(VerticalAlignment.CENTER);
        value = Components.label(Component.empty()).shadow(false);
        value.horizontalSizing(Sizing.fixed(compact ? 44 : 78));
        outer.child(value);
        var arrows = Containers.verticalFlow(Sizing.content(), Sizing.content());
        arrows.gap(1);
        up = Components.button(Component.literal("▲"), button -> step.accept(1));
        down = Components.button(Component.literal("▼"), button -> step.accept(-1));
        up.sizing(Sizing.fixed(18), Sizing.fixed(compact ? 14 : 16));
        down.sizing(Sizing.fixed(18), Sizing.fixed(compact ? 14 : 16));
        arrows.child(up).child(down);
        outer.child(arrows);
        update(0, 0);
    }

    public FlowLayout outer() { return outer; }

    public void update(int quantity, int maximum) {
        value.text(Component.literal((compact ? "Qty: " : "Quantity: ") + quantity));
        up.active = quantity < maximum;
        down.active = quantity > 1;
    }
}
