package com.tankwars.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Immutable map configuration. Match-local mutable state is created from this definition. */
public final class MapDefinition {
    private final String id;
    private final String name;
    private final float width;
    private final float height;
    private final String environmentThemeId;
    private final TerrainProfileDefinition terrainProfile;
    private final List<SpawnPoint> spawnPoints;

    public MapDefinition(
        String id,
        String name,
        float width,
        float height,
        String environmentThemeId,
        TerrainProfileDefinition terrainProfile,
        List<SpawnPoint> spawnPoints) {
        this.id = requireText(id, "Map ID");
        this.name = requireText(name, "Map name");
        this.environmentThemeId = requireText(environmentThemeId, "Environment theme ID");
        if (!Float.isFinite(width) || width <= 0f
            || !Float.isFinite(height) || height <= 0f) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        if (terrainProfile == null) {
            throw new IllegalArgumentException("Terrain profile is required");
        }
        if (terrainProfile.getCoveredWidth() < width) {
            throw new IllegalArgumentException("Terrain profile must cover the full map width");
        }
        if (spawnPoints == null || spawnPoints.isEmpty()) {
            throw new IllegalArgumentException("At least one spawn point is required");
        }

        this.width = width;
        this.height = height;
        this.terrainProfile = terrainProfile;
        this.spawnPoints = validateAndCopySpawns(spawnPoints, width);
    }

    private static List<SpawnPoint> validateAndCopySpawns(
        List<SpawnPoint> source, float mapWidth) {
        List<SpawnPoint> copy = new ArrayList<SpawnPoint>(source.size());
        Set<String> ids = new HashSet<String>();
        for (SpawnPoint spawn : source) {
            if (spawn == null) {
                throw new IllegalArgumentException("Spawn points must not contain null");
            }
            if (!ids.add(spawn.getId())) {
                throw new IllegalArgumentException("Duplicate spawn point ID: " + spawn.getId());
            }
            if (spawn.getX() < 0f || spawn.getX() > mapWidth) {
                throw new IllegalArgumentException("Spawn point is outside the map: " + spawn.getId());
            }
            copy.add(spawn);
        }
        return Collections.unmodifiableList(copy);
    }

    private static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getEnvironmentThemeId() {
        return environmentThemeId;
    }

    public TerrainProfileDefinition getTerrainProfile() {
        return terrainProfile;
    }

    public List<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }
}
