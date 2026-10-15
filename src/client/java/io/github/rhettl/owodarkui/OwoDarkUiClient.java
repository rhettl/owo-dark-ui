package io.github.rhettl.owodarkui;

import io.github.rhettl.owodarkui.playground.PlaygroundScreen;
import io.github.rhettl.owodarkui.playground.PlaygroundTabs;
import io.github.rhettl.owodarkui.playground.tabs.FramesAndTabsTab;
import io.github.rhettl.owodarkui.playground.tabs.InventoryTab;
import io.github.rhettl.owodarkui.playground.tabs.ListWellTab;
import io.github.rhettl.owodarkui.playground.tabs.ThemeTab;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

/** Client entrypoint: binds the playground screen and registers the library's own tabs. */
public final class OwoDarkUiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(OwoDarkUi.PLAYGROUND_MENU, PlaygroundScreen::new);

        PlaygroundTabs.register(PlaygroundTabs.LIBRARY_ORDER, context -> new FramesAndTabsTab());
        PlaygroundTabs.register(PlaygroundTabs.LIBRARY_ORDER, InventoryTab::new);
        PlaygroundTabs.register(PlaygroundTabs.LIBRARY_ORDER, context -> new ListWellTab());
        PlaygroundTabs.register(PlaygroundTabs.LIBRARY_ORDER, context -> new ThemeTab());
    }
}
