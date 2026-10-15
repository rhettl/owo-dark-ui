package io.github.rhettl.owodarkui.theme;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaletteTest {
    @Test
    void brightenAppliesTwoTenPercentPassesAndKeepsAlpha() {
        // 0x1D -> 32 -> 35 (0x23); 0x21 -> 36 -> 40 (0x28); 0x1F -> 34 -> 37 (0x25)
        assertThat(Palette.brighten(0xFF1D211F)).isEqualTo(0xFF232825);
        assertThat(Palette.brighten(0x801D211F) >>> 24).isEqualTo(0x80);
    }

    @Test
    void brightenClampsAtWhite() {
        assertThat(Palette.brighten(0xFFF0F0F0)).isEqualTo(0xFFFFFFFF);
    }

    @Test
    void builderReplacesOnlyTheNamedRoles() {
        var custom = Palette.DARK.toBuilder().accent(0xFF112233).textMuted(0xFF445566).build();

        assertThat(custom.accent()).isEqualTo(0xFF112233);
        assertThat(custom.textMuted()).isEqualTo(0xFF445566);
        assertThat(custom.toBuilder().accent(Palette.DARK.accent()).textMuted(Palette.DARK.textMuted()).build())
            .isEqualTo(Palette.DARK);
    }

    @Test
    void darkKeepsTheUnbrightenedAccentTones() {
        assertThat(Palette.DARK.accent()).isEqualTo(0xFFC9AA56);
        assertThat(Palette.DARK.border()).isEqualTo(0xFF6B6949);
        assertThat(Palette.DARK.textPrimary()).isEqualTo(0xFFF1E9D2);
    }
}
