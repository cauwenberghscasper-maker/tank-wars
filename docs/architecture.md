# Tank Wars architecture

## Scope and assumptions

The first target remains a desktop-tested, shared-core player-versus-bot match. Android and iOS launchers remain thin platform shells. Online play, teams larger than 1v1, account progression, stores, and content breadth are intentionally outside the current milestone.

The design optimizes for deterministic fixed-step gameplay, mobile-friendly runtime costs, isolated content definitions, and small feature slices. It does not introduce an ECS, dependency-injection framework, event bus library, or serializer before those tools solve a concrete problem.

## System boundaries

```text
platform launchers
        |
        v
screen + input adapters ---------> rendering + UI
        |                               |
        v                               | reads snapshots/state
match orchestration --------------------+
        |
        +--> controllers -> commands/intents
        +--> tanks + movement + health
        +--> weapons -> projectile simulation
        +--> impact resolution -> combat damage
        |                     -> terrain deformation
        +--> match progression <- combat/objective events
        |
        v
world instance <- map definition + match definition + content catalog
```

All arrows into gameplay point toward the shared `core` module. Gameplay does not read keyboards, touch screens, platform APIs, or network sockets. Renderers observe state and never decide gameplay outcomes.

## Major systems and responsibilities

### Match

`MatchDefinition` will select the map, mode, participants, teams, controllers, and deterministic seed. `Match`/`GameWorld` owns the fixed-step lifecycle, roster, win conditions, and system update order. Participant identity, team identity, and controller type must be separate; the current `PLAYER`/`BOT` team names are prototype debt because control source is not a team.

### Maps and world

`MapDefinition` is immutable content: stable ID, display name, dimensions, terrain source data, spawn points, and environment theme. A match creates mutable world state from it. Later fields can add obstacle definitions, material regions, hazards, moving-object spawners, and map mechanic IDs without placing map-specific branches in `GameWorld`.

Definitions are code-first today. A future JSON loader should produce the same validated definition objects rather than giving gameplay code a second configuration path.

### Terrain

Terrain needs two layers:

- An immutable source definition owned by a map.
- A match-local mutable surface used by collision and rendering.

The first deformable implementation should be a sampled height field with a `TerrainDeformer` applying explicit deformation commands such as radial craters. It is deterministic, compact, easy to render on mobile, and naturally supports altered slopes and falling tanks. It cannot represent caves or overhangs. If those become requirements, the implementation can change behind the terrain collision/deformation boundary to a chunked occupancy field; buildings and other cover should remain obstacle entities rather than being baked into the ground surface.

Terrain modifications should be representable as ordered events (`deformation type`, center, dimensions, source entity, simulation tick) so a future server or replay can reproduce them.

### Tanks

A tank entity should hold identity and mutable match state. Immutable `TankDefinition` content supplies base stats, dimensions, turret configuration, default weapon loadout, and ability loadout. Focused collaborators own health, movement, weapons, and abilities when their behavior becomes non-trivial. The tank controller consumes controller-neutral commands and must not branch on tank archetype.

Do not add a general-purpose stat graph yet. Introduce typed modifiers when the first timed buff or upgrade needs them, with deterministic ordering and stable modifier IDs.

### Weapons, projectiles, and impacts

`WeaponDefinition` describes firing rules and references a `ProjectileDefinition`. A projectile instance contains only identity, source, definition reference, transform, velocity, lifetime, and behavior state. Reusable behavior modules cover bounce, homing, penetration, child spawning, and delayed detonation when each is first needed.

Collision should emit an impact result. Dedicated resolvers then apply combat damage, knockback, status effects, and terrain deformation. A projectile describes effects; it does not edit terrain or award XP directly.

### Abilities and effects

A tank owns ability instances created from definitions. The common lifecycle is activation validation, target acquisition, cost/cooldown consumption, active duration, effect application, and UI-readable state. Abilities compose typed effects such as stat modifiers, projectile volleys, healing, spawning deployables, and teleportation. Tank-type conditionals are forbidden in the ability runner.

### Match progression

Combat and objective outcomes produce domain events. `MatchProgression` owns per-participant XP, level thresholds, pending choices, and applied upgrade IDs. Upgrade definitions apply typed effects to stats, loadouts, weapons, or abilities. This state is created and discarded with the match. Future account progression belongs outside this boundary and may only influence match setup/content availability.

### Input and future networking

Desktop, touch, bot, replay, and network adapters should all produce the same tick-scoped command shape. Important entities need stable IDs. Randomness must come from match-owned seeded generators, and simulation code must not use frame time or wall-clock time. A future network layer can then submit commands and replicate authoritative snapshots/events without changing tank behavior.

## Dependency rules

- Platform modules may depend on `core`; `core` never depends on a launcher.
- Rendering and UI may read gameplay state but never mutate simulation outcomes.
- Definitions contain data and validation, not match-local mutable state.
- Controllers request actions; weapon/combat/terrain systems resolve them.
- Combat and objectives publish outcomes; progression consumes them.
- Abilities and projectiles reference effect definitions; effects target narrow service interfaces.
- Maps select content by stable ID; the world does not branch on a map ID.

## Decisions that are expensive to change later

1. Terrain representation: a height field is the planned first implementation. Caves/overhangs would require an occupancy or polygon model, so that requirement must be decided before large terrain content investment.
2. Determinism model: fixed-step order, seeded randomness, stable IDs, and explicit events should be established before multiplayer or replays.
3. Identity and teams: entity ID, participant ID, team ID, and controller source must not remain conflated.
4. Content references: definitions need stable IDs before saves, replays, upgrades, or network payloads persist them.
5. Authority boundaries: controllers cannot directly decide hits, damage, XP, or terrain edits if server authority is expected later.
6. Physics engine choice: the current lightweight ballistic math is appropriate for the milestone. Adopting Box2D later would change determinism, collision, and terrain integration and should only happen for a demonstrated need.

## Incremental implementation plan

Each task must leave the game compiling and include focused tests.

1. **Map-definition seam (implemented):** add immutable map, spawn, and terrain-profile definitions; move the existing Grasslands data into a catalog; inject the selected map into `GameWorld`; make terrain, bounds, camera, and projectile lifetime use its dimensions.
2. **Terrain deformation:** replace the immutable surface samples with match-local sampled data, add a deformation command and crater deformer, update tank grounding, and test collision/render queries after deformation.
3. **Projectile and impact definitions:** introduce the normal-cannon projectile definition, stable projectile IDs, impact results, and terrain-effect descriptions while preserving current behavior.
4. **Combat resolution:** move damage application out of `ProjectileManager`, introduce damage events/results, and keep health/death and win evaluation independent.
5. **Tank definitions and identity:** separate entity/team/controller identity; construct current tank stats and turret values from immutable tank definitions.
6. **Controller-neutral commands:** have desktop and bot adapters produce movement, aim, charge, and fire commands consumed at fixed ticks.
7. **Weapon boundary:** give tanks weapon instances created from definitions; controllers request fire rather than creating projectiles.
8. **Ability framework:** implement lifecycle/state contracts and one small example ability only.
9. **Match progression:** add XP/levels and deterministic upgrade choices driven by combat events, explicitly scoped to match lifetime.
10. **Match setup and roster:** replace the hardcoded 1v1 roster with definitions that can express bot matches and later 2v2/3v3 without implementing those modes yet.

## Revisit triggers

- Revisit terrain storage before implementing caves, tunnels, overhangs, or large maps with streaming.
- Revisit client/server state representation before implementing the first network transport.
- Revisit the simple entity composition once deployables, many simultaneous effects, or hundreds of entities make update ownership unclear.
- Add external data files only when a second map or non-programmer content workflow justifies serialization and schema migration.
