package io.github.rhettl.owodarkui.menu;

import io.wispforest.owo.client.screens.SlotGenerator;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * A menu backed entirely by the player's own inventory, for previews and
 * playgrounds. Register a {@link MenuType} per screen that should use it.
 * Slots 0-26 are the main inventory and 27-35 the hotbar.
 */
public class InventoryOnlyMenu extends AbstractContainerMenu {
    public static final int INVENTORY_SLOTS = 27;
    public static final int TOTAL_SLOTS = 36;

    public InventoryOnlyMenu(MenuType<?> type, int syncId, Inventory inventory) {
        super(type, syncId);
        // Client slot components supply the final layout coordinates.
        SlotGenerator.begin(this::addSlot, 0, 0).playerInventory(inventory);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= TOTAL_SLOTS) return ItemStack.EMPTY;
        var slot = getSlot(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem();
        var original = stack.copy();
        boolean moved = index < INVENTORY_SLOTS
            ? moveItemStackTo(stack, INVENTORY_SLOTS, TOTAL_SLOTS, false)
            : moveItemStackTo(stack, 0, INVENTORY_SLOTS, false);
        if (!moved) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
