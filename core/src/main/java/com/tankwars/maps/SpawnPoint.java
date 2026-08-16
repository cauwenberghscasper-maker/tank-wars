package com.tankwars.maps;

/** Immutable map-owned tank spawn data. Vertical placement comes from the terrain surface. */
public final class SpawnPoint {
    private final String id;
    private final float x;
    private final float turretAngleRadians;

    public SpawnPoint(String id, float x, float turretAngleRadians) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Spawn point ID must not be blank");
        }
        if (!Float.isFinite(x)) {
            throw new IllegalArgumentException("Spawn point x must be finite");
        }
        if (!Float.isFinite(turretAngleRadians)) {
            throw new IllegalArgumentException("Spawn point turret angle must be finite");
        }
        this.id = id;
        this.x = x;
        this.turretAngleRadians = turretAngleRadians;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getTurretAngleRadians() {
        return turretAngleRadians;
    }
}
