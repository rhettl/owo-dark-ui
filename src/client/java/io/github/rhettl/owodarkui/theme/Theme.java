package io.github.rhettl.owodarkui.theme;

import io.github.rhettl.owodarkui.OwoDarkUi;
import net.minecraft.resources.ResourceLocation;

/**
 * A palette plus the nine-patch textures that draw frame and well borders.
 *
 * <p>Texture ids name owo nine-patch definitions, loaded from
 * {@code assets/<namespace>/nine_patch_textures/<path>.json}. There are two ways
 * to change a border:
 * <ul>
 *   <li>Point the theme at your own definition, e.g.
 *       {@code Theme.DEFAULT.withTextures(Theme.DEFAULT.textures().withWell(id("my_mod", "inset_well")))}.
 *       This is deterministic and is the recommended way for mods.</li>
 *   <li>Override {@code rhettl-owo-dark-ui:nine_patch_textures/<name>.json} or its PNG
 *       from a resource pack. Two mods shipping the same path resolve by load
 *       order, so use this for resource packs, not mods.</li>
 * </ul>
 */
public record Theme(Palette palette, Textures textures) {
    public static final Theme DEFAULT = new Theme(Palette.DARK, Textures.DEFAULT);

    public Theme withPalette(Palette palette) {
        return new Theme(palette, textures);
    }

    public Theme withTextures(Textures textures) {
        return new Theme(palette, textures);
    }

    /** Nine-patch ids for each frame style and the recessed well. */
    public record Textures(
        ResourceLocation frameGoldRounded,
        ResourceLocation frameDarkRounded,
        ResourceLocation frameGoldSquare,
        ResourceLocation frameDarkSquare,
        ResourceLocation well
    ) {
        public static final Textures DEFAULT = new Textures(
            OwoDarkUi.id("outset_frame"),
            OwoDarkUi.id("outset_frame_dark_rounded"),
            OwoDarkUi.id("outset_frame_gold_square"),
            OwoDarkUi.id("outset_frame_dark_square"),
            OwoDarkUi.id("inset_well"));

        public Textures withFrameGoldRounded(ResourceLocation id) {
            return new Textures(id, frameDarkRounded, frameGoldSquare, frameDarkSquare, well);
        }

        public Textures withFrameDarkRounded(ResourceLocation id) {
            return new Textures(frameGoldRounded, id, frameGoldSquare, frameDarkSquare, well);
        }

        public Textures withFrameGoldSquare(ResourceLocation id) {
            return new Textures(frameGoldRounded, frameDarkRounded, id, frameDarkSquare, well);
        }

        public Textures withFrameDarkSquare(ResourceLocation id) {
            return new Textures(frameGoldRounded, frameDarkRounded, frameGoldSquare, id, well);
        }

        public Textures withWell(ResourceLocation id) {
            return new Textures(frameGoldRounded, frameDarkRounded, frameGoldSquare, frameDarkSquare, id);
        }
    }
}
