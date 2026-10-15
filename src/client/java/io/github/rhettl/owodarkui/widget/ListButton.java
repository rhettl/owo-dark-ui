package io.github.rhettl.owodarkui.widget;

import io.github.rhettl.owodarkui.theme.Themes;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Sizing;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** A focusable item/text list row with no tooltip, built on owo's real button. */
public final class ListButton extends ButtonComponent {
    public static final int HEIGHT = 20;
    private final ItemStack icon;
    private final boolean evenRow;
    private boolean selected;

    public ListButton(ListWell.Entry entry, int index, Consumer<ListWell.Entry> onPress) {
        super(entry.text(), button -> onPress.accept(entry));
        icon = entry.icon().copy();
        evenRow = (index & 1) == 0;
        sizing(Sizing.fill(100), Sizing.fixed(HEIGHT));
        textShadow(false);
    }

    /** Persistent selection; owo's active flag continues to mean enabled. */
    public void selected(boolean selected) {
        this.selected = selected;
    }

    public boolean selected() {
        return selected;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        var palette = Themes.palette();
        int x = getX(), y = getY(), width = getWidth(), height = getHeight();
        boolean focused = active() && isFocused();
        boolean hovered = active() && isHovered();
        boolean highlighted = focused || hovered;
        int background = selected ? palette.selected()
            : highlighted ? palette.panel() : evenRow ? palette.frame() : palette.well();
        graphics.fill(x, y, x + width, y + height, background);
        if (selected || highlighted) {
            graphics.fill(x, y, x + 1, y + height,
                highlighted ? palette.accentHighlight() : palette.accent());
        }

        int textX = x + (icon.isEmpty() ? 4 : 2);
        if (!icon.isEmpty()) {
            graphics.renderItem(icon, textX, y + (height - 16) / 2);
            textX += 21;
        }
        var font = Minecraft.getInstance().font;
        int available = Math.max(0, x + width - 2 - textX);
        var message = getMessage();
        int color = !active() ? palette.textMuted()
            : selected || highlighted ? palette.accentHighlight() : palette.textPrimary();
        if (font.width(message) <= available) {
            graphics.drawString(font, message, textX, y + (height - font.lineHeight) / 2, color, false);
        } else {
            String shortened = font.plainSubstrByWidth(message.getString(), Math.max(0, available - font.width("…")));
            graphics.drawString(font, shortened + "…", textX, y + (height - font.lineHeight) / 2, color, false);
        }
    }
}
