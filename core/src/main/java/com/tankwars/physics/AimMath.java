package com.tankwars.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public final class AimMath {
    private AimMath() {
    }

    /** Returns an angle in the upper hemisphere, including horizontal left and right. */
    public static float upperHemisphereAngle(Vector2 origin, Vector2 target) {
        float dx = target.x - origin.x;
        float dy = target.y - origin.y;
        if (dy < 0f) {
            return dx < 0f ? MathUtils.PI : 0f;
        }
        return MathUtils.clamp(MathUtils.atan2(dy, dx), 0f, MathUtils.PI);
    }
}
