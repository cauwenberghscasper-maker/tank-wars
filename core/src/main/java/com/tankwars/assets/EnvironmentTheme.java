package com.tankwars.assets;

import com.badlogic.gdx.graphics.Color;

/** Immutable visual theme data kept separate from maps and rendering code. */
public final class EnvironmentTheme {
    private final String id;
    private final String backgroundPath;
    private final Color clearColor;
    private final Color terrainBase;
    private final Color terrainShadow;
    private final Color terrainSurface;
    private final Color terrainEdge;
    private final Color terrainDetail;

    public EnvironmentTheme(
        String id,
        String backgroundPath,
        int clearColor,
        int terrainBase,
        int terrainShadow,
        int terrainSurface,
        int terrainEdge,
        int terrainDetail) {
        this.id = requireText(id, "Theme ID");
        this.backgroundPath = requireText(backgroundPath, "Background path");
        this.clearColor = new Color(clearColor);
        this.terrainBase = new Color(terrainBase);
        this.terrainShadow = new Color(terrainShadow);
        this.terrainSurface = new Color(terrainSurface);
        this.terrainEdge = new Color(terrainEdge);
        this.terrainDetail = new Color(terrainDetail);
    }

    private static String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value;
    }

    public String getId() { return id; }
    public String getBackgroundPath() { return backgroundPath; }
    public Color getClearColor() { return clearColor; }
    public Color getTerrainBase() { return terrainBase; }
    public Color getTerrainShadow() { return terrainShadow; }
    public Color getTerrainSurface() { return terrainSurface; }
    public Color getTerrainEdge() { return terrainEdge; }
    public Color getTerrainDetail() { return terrainDetail; }
}
