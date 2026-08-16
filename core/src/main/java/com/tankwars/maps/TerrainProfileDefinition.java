package com.tankwars.maps;

/** Immutable source data used to create a match-local terrain surface. */
public final class TerrainProfileDefinition {
    private final float controlSpacing;
    private final float[] controlHeights;

    public TerrainProfileDefinition(float controlSpacing, float[] controlHeights) {
        if (!Float.isFinite(controlSpacing) || controlSpacing <= 0f) {
            throw new IllegalArgumentException("Terrain control spacing must be positive");
        }
        if (controlHeights == null || controlHeights.length < 2) {
            throw new IllegalArgumentException("Terrain requires at least two control heights");
        }
        this.controlHeights = controlHeights.clone();
        for (float height : this.controlHeights) {
            if (!Float.isFinite(height) || height < 0f) {
                throw new IllegalArgumentException("Terrain control heights must be finite and non-negative");
            }
        }
        this.controlSpacing = controlSpacing;
    }

    public float getControlSpacing() {
        return controlSpacing;
    }

    public float[] copyControlHeights() {
        return controlHeights.clone();
    }

    public float getCoveredWidth() {
        return controlSpacing * (controlHeights.length - 1);
    }
}
