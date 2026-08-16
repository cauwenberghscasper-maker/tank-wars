package com.tankwars.maps;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MapDefinitionTest {
    @Test
    void builtInGrasslandsContainsMapOwnedConfiguration() {
        MapDefinition map = BuiltInMaps.grasslands();

        assertEquals("grasslands", map.getId());
        assertEquals("Grasslands", map.getName());
        assertEquals("grasslands", map.getEnvironmentThemeId());
        assertEquals(4200f, map.getWidth(), 0.001f);
        assertEquals(1500f, map.getHeight(), 0.001f);
        assertEquals(2, map.getSpawnPoints().size());
    }

    @Test
    void terrainProfileDefensivelyCopiesHeightData() {
        float[] heights = { 100f, 200f };
        TerrainProfileDefinition profile = new TerrainProfileDefinition(1000f, heights);

        heights[0] = 999f;
        float[] firstCopy = profile.copyControlHeights();
        float[] secondCopy = profile.copyControlHeights();

        assertEquals(100f, firstCopy[0], 0.001f);
        assertNotSame(firstCopy, secondCopy);
    }

    @Test
    void mapRejectsDuplicateSpawnIds() {
        TerrainProfileDefinition terrain = new TerrainProfileDefinition(
            1000f, new float[] { 100f, 100f });

        assertThrows(IllegalArgumentException.class, () -> new MapDefinition(
            "test",
            "Test",
            1000f,
            600f,
            "test",
            terrain,
            Arrays.asList(
                new SpawnPoint("same", 200f, 0f),
                new SpawnPoint("same", 800f, 0f))));
    }
}
