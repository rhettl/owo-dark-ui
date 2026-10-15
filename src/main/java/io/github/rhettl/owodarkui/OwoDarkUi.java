package io.github.rhettl.owodarkui;

import io.github.rhettl.owodarkui.menu.InventoryOnlyMenu;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/**
 * Common entrypoint: registers the playground menu on both logical sides.
 * The library adds no commands; consumers open the playground with
 * {@link io.github.rhettl.owodarkui.playground.Playground#open}.
 */
public final class OwoDarkUi implements ModInitializer {
    public static final String MOD_ID = "rhettl-owo-dark-ui";

    public static final MenuType<InventoryOnlyMenu> PLAYGROUND_MENU = Registry.register(
        BuiltInRegistries.MENU, id("playground"),
        new MenuType<>((syncId, inventory) -> new InventoryOnlyMenu(OwoDarkUi.PLAYGROUND_MENU, syncId, inventory),
            FeatureFlags.VANILLA_SET));

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        // PLAYGROUND_MENU registers when this class loads.
    }
}
