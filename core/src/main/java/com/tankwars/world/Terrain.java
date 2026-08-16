package com.tankwars.world;

import com.badlogic.gdx.math.MathUtils;
import com.tankwars.maps.TerrainProfileDefinition;

/** Deterministic smooth terrain height field shared by rendering and collision. */
public final class Terrain {
    private final float width;
    private final float controlSpacing;
    private final float[] heights;

    public Terrain(float width, TerrainProfileDefinition profile) {
        if (!Float.isFinite(width) || width <= 0f) {
            throw new IllegalArgumentException("Terrain width must be positive");
        }
        if (profile == null || profile.getCoveredWidth() < width) {
            throw new IllegalArgumentException("Terrain profile must cover the terrain width");
        }
        this.width = width;
        this.controlSpacing = profile.getControlSpacing();
        this.heights = profile.copyControlHeights();
    }

    public static Terrain flat(float width, float height) {
        return new Terrain(width, new TerrainProfileDefinition(width, new float[] { height, height }));
    }

    public float getHeightAt(float worldX) {
        float x = MathUtils.clamp(worldX, 0f, width);
        float position = x / controlSpacing;
        int index = MathUtils.floor(position);
        float t = position - index;
        float p0 = sample(index - 1);
        float p1 = sample(index);
        float p2 = sample(index + 1);
        float p3 = sample(index + 2);
        float t2 = t * t;
        float t3 = t2 * t;
        return 0.5f * ((2f * p1)
            + (-p0 + p2) * t
            + (2f * p0 - 5f * p1 + 4f * p2 - p3) * t2
            + (-p0 + 3f * p1 - 3f * p2 + p3) * t3);
    }

    private float sample(int index) {
        return heights[MathUtils.clamp(index, 0, heights.length - 1)];
    }

    public float getWidth() {
        return width;
    }
}
