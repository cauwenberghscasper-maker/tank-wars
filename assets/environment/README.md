# Environment asset contract

Environment art is organized by theme. A map stores an `environmentThemeId`; `EnvironmentThemes` resolves that ID to its asset paths and terrain palette, and `GameAssets` owns loading. Gameplay never knows file paths or art dimensions.

To add a professional replacement or a new biome:

1. Create `assets/environment/<theme-id>/` and place the source-resolution art there.
2. Register an `EnvironmentTheme` in `EnvironmentThemes` with the background path and matching terrain palette.
3. Reference that theme ID from a `MapDefinition`.
4. Add a dedicated renderer only when the theme needs a genuinely new layer type. Do not put scenery paths or placements in gameplay code.

The current `grasslands/background.png` is drawn as a distant full-viewport layer. Its trees are part of the authored composition, avoiding repeated foreground cutouts. Curved playable ground is rendered from the same height field used by collision, with smooth material bands and deterministic detail.

## Current source and license

- Asset: **Cloudy Green Hills** by Ahmad_KH
- Source: https://opengameart.org/content/cloudy-green-hills
- License: Creative Commons CC0 / public domain dedication
- Original file: `cloudy_mountains.png`, 3840×2160
- Runtime treatment: color tint and a 184-pixel left source crop to remove the source edge band; the checked-in PNG remains at source resolution.

Attribution is not required by CC0, but the source is recorded here for provenance and future art-direction work.
