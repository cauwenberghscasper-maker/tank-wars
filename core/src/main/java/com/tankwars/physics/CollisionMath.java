package com.tankwars.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public final class CollisionMath {
    private CollisionMath() {
    }

    /** Sweeps a projectile center through a rectangle expanded by its radius. */
    public static boolean sweptCircleIntersectsRectangle(
        Vector2 start, Vector2 end, float radius, Rectangle rectangle) {
        float minX = rectangle.x - radius;
        float maxX = rectangle.x + rectangle.width + radius;
        float minY = rectangle.y - radius;
        float maxY = rectangle.y + rectangle.height + radius;

        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float tMin = 0f;
        float tMax = 1f;

        if (Math.abs(dx) < 0.00001f) {
            if (start.x < minX || start.x > maxX) {
                return false;
            }
        } else {
            float tx1 = (minX - start.x) / dx;
            float tx2 = (maxX - start.x) / dx;
            if (tx1 > tx2) {
                float swap = tx1;
                tx1 = tx2;
                tx2 = swap;
            }
            tMin = Math.max(tMin, tx1);
            tMax = Math.min(tMax, tx2);
            if (tMin > tMax) {
                return false;
            }
        }

        if (Math.abs(dy) < 0.00001f) {
            return start.y >= minY && start.y <= maxY;
        }

        float ty1 = (minY - start.y) / dy;
        float ty2 = (maxY - start.y) / dy;
        if (ty1 > ty2) {
            float swap = ty1;
            ty1 = ty2;
            ty2 = swap;
        }
        tMin = Math.max(tMin, ty1);
        tMax = Math.min(tMax, ty2);
        return tMin <= tMax;
    }
}
