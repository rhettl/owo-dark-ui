package io.github.rhettl.owodarkui.playground;

import io.github.rhettl.owodarkui.OwoDarkUi;
import io.github.rhettl.owodarkui.menu.InventoryOnlyMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

/** Server-side entry point for the component playground. */
public final class Playground {
    private Playground() {}

    /** Opens the playground for this player; the title is shown in the playground header. */
    public static void open(ServerPlayer player, Component title) {
        player.openMenu(new SimpleMenuProvider(
            (syncId, inventory, owner) -> new InventoryOnlyMenu(OwoDarkUi.PLAYGROUND_MENU, syncId, inventory),
            title));
    }
}
