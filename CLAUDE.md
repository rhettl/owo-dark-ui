# owo Dark UI Development Guide

Java 21 Fabric 1.21.1 library of themeable owo-lib UI components. Mod id
`rhettl-owo-dark-ui`, Maven `io.github.rhettl:owo-dark-ui`, package
`io.github.rhettl.owodarkui`. Mojang mappings, Architectury Loom (kept identical
to bca-crests so composite builds work).

## Code map

- `main/`: `OwoDarkUi` (playground `MenuType`; no commands), `menu.InventoryOnlyMenu`,
  `playground.Playground` (server-side open).
- `client/theme/`: `Palette` (role-named colors), `Theme` (palette + nine-patch ids),
  `Themes` (active theme).
- `client/widget/`: components. They read `Themes` while drawing; never hard-code colors.
- `client/playground/`: playground screen, tab interface, registry, and the library's own tabs.
- `client/resources/assets/rhettl-owo-dark-ui/`: nine-patch definitions and skin PNGs.

## Conventions

1. Nothing domain-specific (currency, merchants, Cobblemon). Consumers compose
   their own widgets from these primitives.
2. New colors are palette roles, not literals. Vanilla-look variants (`light`) are
   the exception.
3. Public API changes go in `CHANGELOG.md`; bump `mod_version` per semver.
4. Every component gets a playground page (`client/playground/tabs/`) showing its
   states and edge cases. See README "For AI coding agents".
5. JVM tests for pure logic (`Palette`). Rendering and layout need a game client
   through a consumer's playground command. A passing build does not verify visuals.

## Verification

```bash
./gradlew build
./gradlew runClient
```
