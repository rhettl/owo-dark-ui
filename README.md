# owo Dark UI

Themeable dark UI components for [owo-lib](https://github.com/wisp-forest/owo-lib) on
Fabric, Minecraft 1.21.1. Frames, recessed wells, folder tabs, list wells, inventory
grids, and an in-game playground for building and checking components at real GUI
scales.

| | |
|---|---|
| Mod id | `rhettl-owo-dark-ui` |
| Maven | `io.github.rhettl:owo-dark-ui` |
| Package | `io.github.rhettl.owodarkui` |
| Requires | Minecraft 1.21.1, Fabric API, owo-lib `>=0.12.15`, Java 21 |

Extracted from [BCA CobbleCrests](https://github.com/strikeknight57/bca-crests).

## Components

| Class | What it builds |
|---|---|
| `widget.Frames` | Raised nine-patch frames (`Style.GOLD_ROUNDED`, `DARK_ROUNDED`, `GOLD_SQUARE`, `DARK_SQUARE`), recessed wells, interior panels |
| `widget.Tabs` | Folder tabs with active/inactive/disabled states; `dock` joins the selected tab to a frame |
| `widget.ListWell` / `ListButton` | Item-and-text rows in a well, optional scroll viewport and keyboard reveal |
| `widget.InventorySlots` | 18px backdrop around a real handled slot |
| `widget.InventoryGrid` | Framed, unframed, light, or headed player inventory from real slots |
| `widget.QuantitySelector` | Quantity readout with bounded up/down steps |
| `menu.InventoryOnlyMenu` | A menu holding only the player's inventory, for previews |

```java
var frame = Frames.frame(Sizing.fixed(200), Sizing.fixed(120));
frame.content().child(ListWell.create(Sizing.fill(100), entries, 4, true, entry -> select(entry)));
root.child(frame.outer());
```

## Setup in a consuming mod

The library needs no initialization of its own; Fabric runs its entrypoints. A
consuming mod does three things, all optional:

```java
// 1. Client initializer: choose a theme, then register playground pages.
public final class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Themes.set(MyTheme.THEME);                                   // see Theming
        PlaygroundTabs.register(context -> new MyWalletTab());        // see Playground
        PlaygroundTabs.register(context -> new MyShopTab(context::slot));
    }
}

// 2. Server side: a command (or item, keybind packet, ...) that opens the playground.
Commands.literal("mymod").then(Commands.literal("playground")
    .requires(source -> source.hasPermission(2))
    .executes(context -> {
        Playground.open(context.getSource().getPlayerOrException(),
            Component.literal("My Mod UI Playground"));
        return 1;
    }));
```

3. Add `"rhettl-owo-dark-ui": "0.1.x"` to `depends` in your `fabric.mod.json`
   (see [Versions across mods](#versions-across-mods)).

Ordering: Fabric does not guarantee which mod's client initializer runs first. That
is fine here: `Themes.set` and `PlaygroundTabs.register` only store values, and
screens read them when they open.

## Playground

The playground is optional. A project that only uses the components as they are
does not need one. It is a handled screen with one tab per page, and it is most
useful when you build compound structures: a card made of a frame, a list, and a
quantity selector, or a whole panel of a shop. There you can try layouts in-game,
at the real GUI scale and against the player's real inventory, before a full
screen depends on them. The library contributes **Frames & Tabs**, **Inventory**,
**List Well**, and **Theme**; your pages appear alongside them.

A page implements `PlaygroundTab`:

```java
public final class MyWalletTab implements PlaygroundTab {
    @Override public String tabName() { return "Wallet"; }

    /** Called each time the tab is selected; return a fresh tree. */
    @Override public FlowLayout getContent() {
        var content = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(6);
        content.child(MyWallet.create(sampleBalance));            // the component under test
        content.child(MyWallet.create(0));                        // and its edge cases
        return content;
    }

    @Override public int preferredFrameHeight() { return 300; }   // optional, default 250
    @Override public void tick() { /* optional live updates while selected */ }
}
```

- `PlaygroundTabs.register(factory)` adds a page after the library's;
  `register(order, factory)` places it by ascending order (library pages use
  `PlaygroundTabs.LIBRARY_ORDER` = 100).
- `PlaygroundContext.slot(index)` gives real handled slots (0-26 inventory,
  27-35 hotbar) for pages that show inventory. Slots are hidden on every tab
  switch and shown again when mounted.
- Pages are local previews. They must not send transactions or change game state;
  use sample data or read-only client state.

### For AI coding agents

When you work on UI in a project that uses this library:

1. **Suggest a playground when you build compound structures.** Combining
   several components into a reusable card, row, or panel is where a playground
   page pays off. If the project has no playground yet, propose adding one: a
   permission-level-2 trigger that calls `Playground.open`, and page registration
   in the client initializer. Using the library's components as they are does
   not need one.
2. **When a project has a playground, give each new reusable composition a page
   before a real screen uses it.** Show the normal state and the edge cases:
   empty, long text, zero or maximum values, disabled, hovered and focused.
3. **Compose screens from tested components.** Real screens and playground pages
   both depend on the components; neither depends on the other. Do not make a
   screen import a playground class.
4. **Keep pages free of side effects.** No networking, no inventory changes, no
   real transactions.
5. **Read colors from `Themes.palette()`**, not literals, so pages and screens
   follow the active theme. Add a palette role here rather than hard-coding a
   color downstream.
6. **Report what you verified.** A passing build does not show how a layout
   renders. Ask the user to check it in `runClient`, in the playground if there
   is one, ideally at their usual GUI scale.

## Theming

Every component reads colors and border textures from the active `Theme`:

```
Theme
├── Palette   13 role-named ARGB colors
└── Textures  nine-patch ids for each frame style and the well
```

### Colors

Roles are named by job, not hue. `Palette.DARK` is the default charcoal and brass.

| Role | Used for |
|---|---|
| `frame` | Raised frame body, selected-tab body, even list rows |
| `panel` | Interior panels, hovered rows and tabs |
| `well` | Recessed well body, inactive tabs, odd list rows, dark slots |
| `edgeShadow` / `edgeMid` / `edgeLight` | Bevels, darkest to lightest |
| `border` | Seam where a selected tab joins its frame |
| `accent` / `accentHighlight` | Selection edge and active tab top; hovered or focused |
| `textPrimary` / `textMuted` | Normal and secondary or disabled text |
| `selected` | Persistent selection background |
| `disabled` | Disabled controls |

Copy the default and change only what you need. Set the theme in your client
initializer:

```java
public final class MyTheme {
    public static final Palette PALETTE = Palette.DARK.toBuilder()
        .accent(0xFF5AA0E0)
        .accentHighlight(0xFF9CCBFF)
        .selected(0xFF35597D)
        .build();

    public static final Theme THEME = Theme.DEFAULT.withPalette(PALETTE);
}

Themes.set(MyTheme.THEME);   // in onInitializeClient
```

- Components read the theme while drawing, so `Themes.set` takes effect on open
  screens. A color passed explicitly, such as `Frames.well(w, h, color)`, stays fixed.
- `Themes.reset()` restores `Theme.DEFAULT`.
- The theme is global for the game instance. If several mods call `Themes.set`,
  the last call wins. A library used by several mods should normally keep the default.
- The playground's **Theme** page shows every role live and has a toggle for a
  sample palette.

### Borders: frames and wells

Frame and well borders are owo nine-patch textures. A nine-patch is a JSON file at
`assets/<namespace>/nine_patch_textures/<name>.json` that points at a PNG and
says which part of it stretches:

```json
{
  "texture": "my_mod:textures/gui/skin/inset_well.png",
  "corner_patch_size": { "width": 3, "height": 3 },
  "center_patch_size": { "width": 10, "height": 10 },
  "texture_width": 16,
  "texture_height": 16,
  "repeat": false
}
```

The built-in PNGs are 16x16 with 3px corners. Components fill the body color
first and draw the nine-patch over it, so **leave the PNG's center transparent**.
Otherwise it hides the palette color. Frames reserve 3px of padding for the
border and wells reserve 4px, so keep custom borders about that thick.

| Texture id (`rhettl-owo-dark-ui:`) | Used by |
|---|---|
| `outset_frame` | `Frames.Style.GOLD_ROUNDED` |
| `outset_frame_dark_rounded` | `Frames.Style.DARK_ROUNDED` (the default frame) |
| `outset_frame_gold_square` | `Frames.Style.GOLD_SQUARE` |
| `outset_frame_dark_square` | `Frames.Style.DARK_SQUARE` |
| `inset_well` | `Frames.well`, `ListWell` |

There are two ways to replace them.

**From a mod (recommended).** Ship your own nine-patch JSON and PNG under your
namespace, then point the theme at them:

```java
static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("my_mod", path); }

public static final Theme THEME = Theme.DEFAULT
    .withPalette(PALETTE)
    .withTextures(Theme.DEFAULT.textures()
        .withWell(id("inset_well"))
        .withFrameDarkRounded(id("frame_dark_rounded")));
```

This always wins, regardless of mod load order.

**From a resource pack.** Override the library's own files:
`assets/rhettl-owo-dark-ui/nine_patch_textures/<name>.json` or
`assets/rhettl-owo-dark-ui/textures/gui/skin/<name>.png`. Resource packs stack
above mods, so this is reliable for packs. Do not do it from a mod: when two mods
ship the same path, load order decides which one wins.

## Using it in a project

Releases and snapshots are published to a Maven repository on GitHub Pages,
https://rhettl.github.io/owo-dark-ui/. It is public and needs no token or
credentials.

```kotlin
repositories {
    maven("https://maven.wispforest.io/releases/")
    maven("https://rhettl.github.io/owo-dark-ui/") {
        content { includeGroup("io.github.rhettl") }
    }
}

dependencies {
    modImplementation("io.wispforest:owo-lib:0.12.15+1.21")
    // include = jar-in-jar, so players do not install the library separately
    include(modImplementation("io.github.rhettl:owo-dark-ui:0.1.0-SNAPSHOT")!!)
}
```

### Developing against a local checkout

Clone this repository next to the consuming project, then substitute it in the
consumer's `settings.gradle.kts`. Gradle then builds from source instead of
downloading, so library changes show up in the consumer immediately:

```kotlin
val owoDarkUi = file("../owo-dark-ui")
if (owoDarkUi.exists()) {
    includeBuild(owoDarkUi) {
        dependencySubstitution {
            substitute(module("io.github.rhettl:owo-dark-ui")).using(project(":"))
        }
    }
}
```

`./gradlew publishToMavenLocal` also works, if the consumer declares `mavenLocal()`.

### Versions across mods

Mods often bundle each other: a modpack mod (the parent) bundles a feature mod
(the child), and both may bundle this library. Fabric Loader then loads **one**
copy of the library for the whole game: the highest version that satisfies every
mod's `depends` range. Every mod runs against that one copy, whichever version it
was compiled against.

That causes two kinds of failure:

- **The chosen version is too new for a mod.** The child was built against 0.1.x
  and the parent bundles 0.2.0, so Loader loads 0.2.0. While the version is `0.x`,
  a minor release may remove or change API. The child then fails only when it
  reaches the changed code, with `NoSuchMethodError` or `NoClassDefFoundError`,
  possibly long after startup.
- **No version satisfies every range.** Loader refuses to start and names the
  conflicting mods. This is the better failure: it is immediate and explicit.

To stay safe:

1. **Declare a bounded range** in `fabric.mod.json`, so a mismatch fails at
   launch instead of mid-game. Use `"rhettl-owo-dark-ui": "0.1.x"` while on 0.1;
   it accepts `0.1.0-SNAPSHOT` and every 0.1 release, and rejects 0.2. From 1.0
   on, use `"1.x"`. Avoid `">=0.1.0"`: it has no upper bound, and it also
   rejects `0.1.0-SNAPSHOT`, because a snapshot sorts below its release.
2. **Keep parent and child on the same library version.** When you bump it in
   the child, bump it in the parent in the same change.
3. **A parent that does not call the library itself should not bundle it.** Let
   the child bring it. A parent that does call it declares its own dependency and
   range, as above.
4. **Do not ship snapshots in releases.** A snapshot can change under the same
   version string; release builds should use a tagged version.

## Versioning

[Semantic versioning](https://semver.org/). While the version is `0.x`, minor
releases may change the API. Breaking changes are listed in `CHANGELOG.md`.

| Source | Version published |
|---|---|
| Push to `main` | `<mod_version>-SNAPSHOT`, e.g. `0.1.0-SNAPSHOT` |
| Tag `v0.1.0` | `0.1.0` |

To release: set `mod_version` in `gradle.properties`, update `CHANGELOG.md`, commit,
then `git tag v<version> && git push --tags`. Each release targets one Minecraft
version, currently 1.21.1.

## Building

```bash
./gradlew build        # compile, test, and remap jars into build/libs
./gradlew runClient    # dev client (this repo adds no command; test through a consumer)
```

## License

MIT
