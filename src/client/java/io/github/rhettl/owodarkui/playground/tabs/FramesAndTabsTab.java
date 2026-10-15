package io.github.rhettl.owodarkui.playground.tabs;

import io.github.rhettl.owodarkui.playground.PlaygroundTab;
import io.github.rhettl.owodarkui.theme.Themes;
import io.github.rhettl.owodarkui.widget.Frames;
import io.github.rhettl.owodarkui.widget.Tabs;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** The frame/panel foundation and tab interaction states. */
public final class FramesAndTabsTab implements PlaygroundTab {
    private String selectedExample = "Buy";
    private FlowLayout exampleTabHost;
    private final Map<String, ButtonComponent> exampleTabs = new LinkedHashMap<>();

    @Override
    public String tabName() {
        return "Frames & Tabs";
    }

    @Override
    public FlowLayout getContent() {
        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(5);
        content.child(Components.label(Component.literal("FRAMES + PANELS")
            .withStyle(ChatFormatting.GOLD)).shadow(false));

        var frameExamples = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(42));
        frameExamples.gap(4).horizontalAlignment(HorizontalAlignment.CENTER);
        addFrameExample(frameExamples, "Gold / round", Frames.Style.GOLD_ROUNDED);
        addFrameExample(frameExamples, "Dark / round", Frames.Style.DARK_ROUNDED);
        addFrameExample(frameExamples, "Gold / square", Frames.Style.GOLD_SQUARE);
        addFrameExample(frameExamples, "Dark / square", Frames.Style.DARK_SQUARE);
        content.child(frameExamples);

        content.child(Components.label(Component.literal("RECESSED WELLS")
            .withStyle(ChatFormatting.GOLD)).shadow(false));
        var wells = Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(39));
        wells.gap(5).horizontalAlignment(HorizontalAlignment.CENTER);
        addWellExample(wells, "Standard well", Frames.well(Sizing.expand(50), Sizing.fill(100)));
        addWellExample(wells, "Tinted body",
            Frames.well(Sizing.expand(50), Sizing.fill(100), Themes.palette().panel()));
        content.child(wells);

        content.child(Components.label(Component.literal("ATTACHED TABS")
            .withStyle(ChatFormatting.GOLD)).shadow(false));
        var tabExample = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        exampleTabHost = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        exampleTabHost.gap(2).horizontalAlignment(HorizontalAlignment.LEFT);
        exampleTabHost.padding(Insets.left(10));
        tabExample.child(exampleTabHost);
        var preview = Frames.frame(Sizing.fill(100), Sizing.fixed(38));
        preview.content().padding(Insets.of(4));
        preview.content().child(Components.label(Component.literal("Select with click, Tab + Enter, or Space")
            .withStyle(ChatFormatting.GRAY)).shadow(false));
        Tabs.dock(preview, () -> exampleTabs.get(selectedExample));
        tabExample.child(preview.outer());
        content.child(tabExample);
        rebuildExamples();
        return content;
    }

    private static void addFrameExample(FlowLayout host, String label, Frames.Style style) {
        var frame = Frames.frame(Sizing.expand(25), Sizing.fill(100), style);
        frame.content().padding(Insets.of(3));
        frame.content().horizontalAlignment(HorizontalAlignment.CENTER);
        frame.content().verticalAlignment(VerticalAlignment.CENTER);
        var text = Components.label(Component.literal(label).withStyle(ChatFormatting.WHITE)).shadow(false);
        text.horizontalSizing(Sizing.fill(100));
        text.horizontalTextAlignment(HorizontalAlignment.CENTER);
        frame.content().child(text);
        host.child(frame.outer());
    }

    private static void addWellExample(FlowLayout host, String label, Frames.Well well) {
        well.content().horizontalAlignment(HorizontalAlignment.CENTER);
        well.content().verticalAlignment(VerticalAlignment.CENTER);
        well.content().child(Components.label(Component.literal(label)
            .withStyle(ChatFormatting.GRAY)).shadow(false));
        host.child(well.outer());
    }

    private void rebuildExamples() {
        exampleTabHost.clearChildren();
        exampleTabs.clear();
        addExample("Buy", Tabs.State.ACTIVE);
        addExample("Sell", Tabs.State.INACTIVE);
        addExample("Disabled", Tabs.State.DISABLED);
    }

    private void addExample(String label, Tabs.State defaultState) {
        var state = defaultState == Tabs.State.DISABLED ? defaultState
            : label.equals(selectedExample) ? Tabs.State.ACTIVE : Tabs.State.INACTIVE;
        var tab = Tabs.tab(label, state, () -> {
            selectedExample = label;
            exampleTabs.forEach((name, button) -> Tabs.state(button,
                name.equals("Disabled") ? Tabs.State.DISABLED
                    : name.equals(selectedExample) ? Tabs.State.ACTIVE : Tabs.State.INACTIVE));
        });
        exampleTabs.put(label, tab);
        exampleTabHost.child(tab);
    }
}
