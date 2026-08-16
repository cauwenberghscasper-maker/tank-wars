package com.tankwars.physics;

import com.badlogic.gdx.math.Vector2;

public final class Ballistics {
    private Ballistics() {
    }

    /** Solves the lower ballistic arc for a target on either horizontal side. */
    public static float solveLowArc(Vector2 origin, Vector2 target, float speed, float gravityMagnitude) {
        float x = Math.abs(target.x - origin.x);
        float y = target.y - origin.y;
        if (x < 0.001f || speed <= 0f || gravityMagnitude <= 0f) {
            return (float) Math.PI * 0.5f;
        }

        float speedSquared = speed * speed;
        float discriminant = speedSquared * speedSquared
            - gravityMagnitude * (gravityMagnitude * x * x + 2f * y * speedSquared);
        if (discriminant < 0f) {
            return Float.NaN;
        }

        float tangent = (speedSquared - (float) Math.sqrt(discriminant)) / (gravityMagnitude * x);
        return (float) Math.atan(tangent);
    }
}
