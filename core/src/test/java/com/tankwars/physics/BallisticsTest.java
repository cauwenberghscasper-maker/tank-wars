package com.tankwars.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BallisticsTest {
    @Test
    void solvesFortyFiveDegreesAtMaximumFlatRange() {
        float speed = 100f;
        float gravity = 10f;
        float angle = Ballistics.solveLowArc(
            new Vector2(0f, 0f), new Vector2(1000f, 0f), speed, gravity);

        assertEquals(MathUtils.PI * 0.25f, angle, 0.001f);
    }

    @Test
    void reportsUnreachableTarget() {
        float angle = Ballistics.solveLowArc(
            new Vector2(0f, 0f), new Vector2(2000f, 0f), 100f, 10f);

        assertTrue(Float.isNaN(angle));
    }
}
