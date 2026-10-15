# Changelog

All notable changes are listed here. Versions follow [Semantic Versioning](https://semver.org/);
while the version is `0.x`, minor versions may break the API.

## [Unreleased]

## [0.1.0]

First release, extracted from the BCA CobbleCrests UI playground.

### Added
- `Frames`: nine-patch raised frames in four styles, recessed wells, interior panels.
- `Tabs`: folder tabs with active, inactive, and disabled states, plus docking to a frame.
- `ListWell` and `ListButton`: item-and-text rows with an optional scroll viewport.
- `InventorySlots` and `InventoryGrid`: dark or light backdrops for real handled slots.
- `QuantitySelector`: bounded quantity steps.
- `Palette`, `Theme`, and `Themes`: role-named colors and nine-patch texture ids that can be swapped at runtime.
- Playground: `PlaygroundScreen`, `PlaygroundTab`, `PlaygroundTabs` for other mods' tabs, the
  `InventoryOnlyMenu`-backed `Playground.open`, and the Frames & Tabs, Inventory, List Well,
  and Theme pages. The library registers no commands; consumers open the playground.
