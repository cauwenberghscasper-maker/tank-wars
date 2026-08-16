package com.tankwars.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionMathTest {
    private final Rectangle tank = new Rectangle(100f, 100f, 50f, 50f);

    @Test
    void catchesFastProjectileCrossingTank() {
        assertTrue(CollisionMath.sweptCircleIntersectsRectangle(
            new Vector2(0f, 125f), new Vector2(250f, 125f), 5f, tank));
    }

    @Test
    void rejectsProjectilePassingAboveTank() {
        assertFalse(CollisionMath.sweptCircleIntersectsRectangle(
            new Vector2(0f, 200f), new Vector2(250f, 200f), 5f, tank));
    }
}
