package com.tankwars.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AimMathTest {
    private final Vector2 origin = new Vector2(10f, 10f);

    @Test
    void aimsAcrossUpperHemisphere() {
        assertEquals(0f, AimMath.upperHemisphereAngle(origin, new Vector2(20f, 10f)), 0.001f);
        assertEquals(MathUtils.PI * 0.5f,
            AimMath.upperHemisphereAngle(origin, new Vector2(10f, 20f)), 0.001f);
        assertEquals(MathUtils.PI,
            AimMath.upperHemisphereAngle(origin, new Vector2(0f, 10f)), 0.001f);
    }

    @Test
    void belowGroundAimClampsToNearestHorizontalDirection() {
        assertEquals(0f, AimMath.upperHemisphereAngle(origin, new Vector2(20f, 0f)), 0.001f);
        assertEquals(MathUtils.PI,
            AimMath.upperHemisphereAngle(origin, new Vector2(0f, 0f)), 0.001f);
    }
}
