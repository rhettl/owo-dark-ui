package io.github.rhettl.owodarkui.playground;

import io.github.rhettl.owodarkui.menu.InventoryOnlyMenu;
import io.github.rhettl.owodarkui.widget.Frames;
import io.github.rhettl.owodarkui.widget.Tabs;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component.FocusSource;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Catalog shell that displays every registered {@link PlaygroundTab}. */
public class PlaygroundScreen extends BaseOwoHandledScreen<FlowLayout, InventoryOnlyMenu> {

    private final List<PlaygroundTab> tabs;
    private PlaygroundTab selected;
    private FlowLayout tabBar;
    private ScrollContainer<FlowLayout> tabViewport;
    private FlowLayout contentHost;
    private final Map<PlaygroundTab, ButtonComponent> navigationTabs = new LinkedHashMap<>();
    private FlowLayout frameOuter;
    private LabelComponent scaleReadout;

    public PlaygroundScreen(InventoryOnlyMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        tabs = PlaygroundTabs.create(this::slotAsComponent);
        selected = tabs.isEmpty() ? null : tabs.get(0);
    }

    @Override
    protected OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void init() {
        this.imageWidth = frameWidth();
        this.imageHeight = frameHeight() + 22;
        super.init();
        if (frameOuter == null || scaleReadout == null) return;
        tabViewport.horizontalSizing(Sizing.fixed(frameWidth()));
        frameOuter.sizing(Sizing.fixed(frameWidth()), Sizing.fixed(frameHeight()));
        scaleReadout.text(scaleDescription());
    }

    private Component scaleDescription() {
        return Component.literal("GUI scale " + Minecraft.getInstance().getWindow().getGuiScale()
            + " | " + this.width + " x " + this.height + " logical px").withStyle(ChatFormatting.GRAY);
    }

    private int frameWidth() {
        return Math.min(460, Math.max(1, this.width - 16));
    }

    private int frameHeight() {
        int preferred = selected == null ? 250 : selected.preferredFrameHeight();
        return Math.min(preferred, Math.max(1, this.height - 40));
    }

    private void hideInventorySlots() {
        for (int index = 0; index < menu.slots.size(); index++) disableSlot(index);
    }

    @Override
    protected void build(FlowLayout root) {
        root.surface(Surface.VANILLA_TRANSLUCENT);
        root.horizontalAlignment(HorizontalAlignment.CENTER);
        root.verticalAlignment(VerticalAlignment.CENTER);

        tabBar = Containers.horizontalFlow(Sizing.content(), Sizing.fixed(22));
        tabBar.gap(2).horizontalAlignment(HorizontalAlignment.LEFT);
        tabBar.padding(Insets.left(10));
        tabViewport = Containers.horizontalScroll(Sizing.fixed(frameWidth()), Sizing.fixed(22), tabBar);
        tabViewport.scrollbarThiccness(0);
        root.child(tabViewport);

        var frame = Frames.frame(Sizing.fixed(frameWidth()), Sizing.fixed(frameHeight()), Frames.Style.DARK_ROUNDED);
        frameOuter = frame.outer();
        root.child(frame.outer());
        Tabs.dock(frame, () -> navigationTabs.get(selected));

        var titleBar = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
        titleBar.gap(8).verticalAlignment(VerticalAlignment.CENTER);
        var heading = Components.label(Component.literal(title.getString().toUpperCase(Locale.ROOT))
            .withStyle(ChatFormatting.GOLD)).shadow(false);
        heading.horizontalSizing(Sizing.expand(100));
        titleBar.child(heading);
        titleBar.child(Components.button(Component.literal("Close"), button -> this.onClose()));
        frame.content().child(titleBar);
        scaleReadout = Components.label(scaleDescription()).shadow(false);
        frame.content().child(scaleReadout);

        contentHost = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        // Borrow 8px from the frame's 10px right padding, then reserve that same
        // width for the scrollbar and gap so the page keeps its original width.
        contentHost.padding(Insets.right(8));
        var scroll = Containers.verticalScroll(Sizing.fill(100), Sizing.expand(100), contentHost);
        scroll.margins(Insets.right(-8));
        // Let the scroll area occupy the padding; it still clips its own content,
        // and the outer frame continues clipping at its inner border.
        frame.content().allowOverflow(true);
        frame.content().child(scroll);
        rebuildShell();
    }

    private void selectTab(PlaygroundTab tab) {
        if (tab == selected) return;
        selected = tab;
        navigationTabs.forEach((page, button) -> Tabs.state(button,
            page == selected ? Tabs.State.ACTIVE : Tabs.State.INACTIVE));
        this.imageHeight = frameHeight() + 22;
        this.topPos = (this.height - this.imageHeight) / 2;
        frameOuter.verticalSizing(Sizing.fixed(frameHeight()));
        revealTab(navigationTabs.get(selected));
        rebuildContent();
    }

    private void rebuildShell() {
        tabBar.clearChildren();
        navigationTabs.clear();
        for (PlaygroundTab tab : tabs) {
            var state = tab == selected ? Tabs.State.ACTIVE : Tabs.State.INACTIVE;
            var button = Tabs.tab(tab.tabName(), state, () -> selectTab(tab));
            button.focusGained().subscribe(source -> {
                if (source == FocusSource.KEYBOARD_CYCLE) revealTab(button);
            });
            navigationTabs.put(tab, button);
            tabBar.child(button);
        }
        rebuildContent();
    }

    private void revealTab(ButtonComponent button) {
        if (button.getX() < tabViewport.x()
            || button.getX() + button.getWidth() > tabViewport.x() + tabViewport.width()) {
            tabViewport.scrollTo(button);
        }
    }

    private void rebuildContent() {
        // Unmounted slot components no longer update their visibility. Disable
        // every slot first; the mounted page enables its own slots.
        hideInventorySlots();
        contentHost.clearChildren();
        if (selected != null) contentHost.child(selected.getContent());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Titles and inventory labels belong to the component tree.
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (selected != null) selected.tick();
    }
}
