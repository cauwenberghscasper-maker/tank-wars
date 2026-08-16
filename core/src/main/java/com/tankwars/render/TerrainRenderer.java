package com.tankwars.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tankwars.assets.EnvironmentTheme;
import com.tankwars.world.Terrain;

/** Smooth, deterministic terrain material that follows the collision height field exactly. */
public final class TerrainRenderer {
    private static final float SAMPLE_STEP = 12f;
    private static final float WORLD_BOTTOM = -80f;
    private static final float SHADOW_DEPTH = 62f;
    private static final float SURFACE_DEPTH = 28f;
    private static final float EDGE_DEPTH = 5f;

    private final EnvironmentTheme theme;

    public TerrainRenderer(EnvironmentTheme theme) {
        this.theme = theme;
    }

    public void drawFilled(ShapeRenderer shapes, Terrain terrain, float worldWidth) {
        shapes.setColor(theme.getTerrainBase());
        forEachSegment(shapes, terrain, worldWidth, WORLD_BOTTOM, WORLD_BOTTOM);

        shapes.setColor(theme.getTerrainShadow());
        drawBand(shapes, terrain, worldWidth, SHADOW_DEPTH);

        shapes.setColor(theme.getTerrainSurface());
        drawBand(shapes, terrain, worldWidth, SURFACE_DEPTH);

        shapes.setColor(theme.getTerrainEdge());
        drawBand(shapes, terrain, worldWidth, EDGE_DEPTH);

        drawSoilDetail(shapes, terrain, worldWidth);
    }

    private void drawBand(
        ShapeRenderer shapes, Terrain terrain, float worldWidth, float depth) {
        for (float x = 0f; x < worldWidth; x += SAMPLE_STEP) {
            float nextX = Math.min(x + SAMPLE_STEP, worldWidth);
            float topLeft = terrain.getHeightAt(x);
            float topRight = terrain.getHeightAt(nextX);
            drawQuad(shapes, x, topLeft - depth, x, topLeft,
                nextX, topRight, nextX, topRight - depth);
        }
    }

    private void forEachSegment(
        ShapeRenderer shapes,
        Terrain terrain,
        float worldWidth,
        float bottomLeft,
        float bottomRight) {
        for (float x = 0f; x < worldWidth; x += SAMPLE_STEP) {
            float nextX = Math.min(x + SAMPLE_STEP, worldWidth);
            drawQuad(shapes, x, bottomLeft, x, terrain.getHeightAt(x),
                nextX, terrain.getHeightAt(nextX), nextX, bottomRight);
        }
    }

    private void drawSoilDetail(ShapeRenderer shapes, Terrain terrain, float worldWidth) {
        shapes.setColor(theme.getTerrainDetail());
        for (float x = 34f; x < worldWidth; x += 58f) {
            float noise = pseudoRandom(x);
            float depth = 48f + noise * 165f;
            float y = terrain.getHeightAt(x) - depth;
            if (y > 12f) {
                float radius = 2.2f + pseudoRandom(x + 19f) * 3.2f;
                shapes.ellipse(x, y, radius * 2.4f, radius, 10);
            }
        }
    }

    private float pseudoRandom(float value) {
        double sine = Math.sin(value * 12.9898d) * 43758.5453d;
        return (float) (sine - Math.floor(sine));
    }

    private void drawQuad(
        ShapeRenderer shapes,
        float x1, float y1,
        float x2, float y2,
        float x3, float y3,
        float x4, float y4) {
        shapes.triangle(x1, y1, x2, y2, x3, y3);
        shapes.triangle(x1, y1, x3, y3, x4, y4);
    }

    public void drawOutline(ShapeRenderer shapes, Terrain terrain, float worldWidth) {
        shapes.setColor(theme.getTerrainEdge());
        float lastX = 0f;
        float lastY = terrain.getHeightAt(0f);
        for (float x = SAMPLE_STEP; x <= worldWidth; x += SAMPLE_STEP) {
            float sampleX = Math.min(x, worldWidth);
            float y = terrain.getHeightAt(sampleX);
            shapes.line(lastX, lastY, sampleX, y);
            lastX = sampleX;
            lastY = y;
        }
    }
}
