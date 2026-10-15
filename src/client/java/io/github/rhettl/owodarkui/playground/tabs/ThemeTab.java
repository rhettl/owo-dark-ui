package io.github.rhettl.owodarkui.playground.tabs;

import io.github.rhettl.owodarkui.playground.PlaygroundTab;
import io.github.rhettl.owodarkui.theme.Palette;
import io.github.rhettl.owodarkui.theme.Theme;
import io.github.rhettl.owodarkui.theme.Themes;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Live swatches for every palette role, and a toggle that proves themes apply to open screens. */
public final class ThemeTab implements PlaygroundTab {
    /** A deliberately different palette for checking that every component follows the theme. */
    private static final Palette SLATE = Palette.DARK.toBuilder()
        .border(0xFF4E6A86).accent(0xFF5AA0E0).accentHighlight(0xFF9CCBFF).selected(0xFF35597D)
        .build();

    private static final Map<String, ToIntFunction<Palette>> ROLES = new LinkedHashMap<>();

    static {
        ROLES.put("frame", Palette::frame);
        ROLES.put("panel", Palette::panel);
        ROLES.put("well", Palette::well);
        ROLES.put("edgeShadow", Palette::edgeShadow);
        ROLES.put("edgeMid", Palette::edgeMid);
        ROLES.put("edgeLight", Palette::edgeLight);
        ROLES.put("border", Palette::border);
        ROLES.put("accent", Palette::accent);
        ROLES.put("accentHighlight", Palette::accentHighlight);
        ROLES.put("textPrimary", Palette::textPrimary);
        ROLES.put("textMuted", Palette::textMuted);
        ROLES.put("selected", Palette::selected);
        ROLES.put("disabled", Palette::disabled);
    }

    private Theme previous;

    @Override public String tabName() { return "Theme"; }

    @Override
    public FlowLayout getContent() {
        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(5);
        content.child(Components.label(Component.literal("PALETTE ROLES")
            .withStyle(ChatFormatting.GOLD)).shadow(false));

        var grid = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        grid.gap(2);
        FlowLayout row = null;
        int column = 0;
        for (var role : ROLES.entrySet()) {
            if (column++ % 3 == 0) {
                row = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
                row.gap(4);
                grid.child(row);
            }
            row.child(swatch(role.getKey(), role.getValue()));
        }
        content.child(grid);

        content.child(Components.button(Component.literal("Toggle sample slate accent"), button -> {
            if (previous == null) {
                previous = Themes.current();
                Themes.set(previous.withPalette(SLATE));
            } else {
                Themes.set(previous);
                previous = null;
            }
        }));
        content.child(Components.label(Component.literal(
                "Switch, then visit other tabs: theme-driven colors change without rebuilding.")
            .withStyle(ChatFormatting.GRAY)).shadow(false));
        return content;
    }

    private static FlowLayout swatch(String name, ToIntFunction<Palette> role) {
        var cell = Containers.horizontalFlow(Sizing.expand(33), Sizing.fixed(16));
        cell.gap(4).verticalAlignment(VerticalAlignment.CENTER);
        var chip = Containers.verticalFlow(Sizing.fixed(16), Sizing.fixed(16));
        chip.surface((context, component) -> {
            int x = component.x(), y = component.y();
            context.fill(x, y, x + 16, y + 16, Themes.palette().edgeShadow());
            context.fill(x + 1, y + 1, x + 15, y + 15, role.applyAsInt(Themes.palette()));
        });
        cell.child(chip);
        cell.child(Components.label(Component.literal(name).withStyle(ChatFormatting.GRAY)).shadow(false));
        return cell;
    }
}
