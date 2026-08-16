package com.tankwars.world;

import com.tankwars.maps.BuiltInMaps;
import com.tankwars.maps.MapDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerrainTest {
    @Test
    void curvedTerrainStaysInsidePlayableVerticalBand() {
        MapDefinition map = BuiltInMaps.grasslands();
        Terrain terrain = new Terrain(map.getWidth(), map.getTerrainProfile());
        for (float x = 0f; x <= map.getWidth(); x += 25f) {
            float height = terrain.getHeightAt(x);
            assertTrue(height >= 140f && height <= 380f);
        }
    }

    @Test
    void flatTerrainReturnsSameHeightEverywhere() {
        Terrain terrain = Terrain.flat(1000f, 123f);
        assertEquals(123f, terrain.getHeightAt(0f), 0.001f);
        assertEquals(123f, terrain.getHeightAt(1000f), 0.001f);
    }
}
