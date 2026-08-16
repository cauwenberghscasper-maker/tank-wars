package com.tankwars.maps;

import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

/** Code-first map catalog. A JSON loader can produce the same definitions later. */
public final class BuiltInMaps {
    private static final MapDefinition GRASSLANDS = new MapDefinition(
        "grasslands",
        "Grasslands",
        4200f,
        1500f,
        "grasslands",
        new TerrainProfileDefinition(300f, new float[] {
            180f, 205f, 165f, 235f, 325f,
            250f, 175f, 215f, 350f, 310f,
            185f, 230f, 305f, 195f, 225f
        }),
        Arrays.asList(
            new SpawnPoint("west", 430f, MathUtils.PI * 0.25f),
            new SpawnPoint("east", 3770f, MathUtils.PI * 0.75f)));

    private BuiltInMaps() {
    }

    public static MapDefinition grasslands() {
        return GRASSLANDS;
    }
}
