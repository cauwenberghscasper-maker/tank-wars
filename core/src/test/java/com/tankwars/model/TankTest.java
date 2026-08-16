package com.tankwars.model;

import com.badlogic.gdx.math.MathUtils;
import com.tankwars.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TankTest {
    private static final float TEST_WORLD_WIDTH = 1000f;

    @Test
    void damageNeverDropsHealthBelowZero() {
        Tank tank = new Tank(500f, 100f, Team.PLAYER, 0f);

        tank.takeDamage(125);

        assertEquals(0, tank.getHealth());
        assertFalse(tank.isAlive());
    }

    @Test
    void movementUsesDeltaAndClampsToArena() {
        Tank tank = new Tank(500f, 100f, Team.PLAYER, 0f);
        tank.setMovementDirection(-1f);

        tank.update(1f, TEST_WORLD_WIDTH);

        assertEquals(500f - GameConfig.TANK_SPEED, tank.getPosition().x, 0.001f);
        tank.update(10f, TEST_WORLD_WIDTH);
        assertEquals(GameConfig.TANK_WIDTH * 0.5f, tank.getPosition().x, 0.001f);
    }

    @Test
    void turretCannotAimUnderGround() {
        Tank tank = new Tank(500f, 100f, Team.PLAYER, -MathUtils.PI * 0.25f);
        assertEquals(0f, tank.getTurretAngle(), 0.001f);

        tank.setTurretAngle(MathUtils.PI2);
        assertEquals(MathUtils.PI, tank.getTurretAngle(), 0.001f);
    }
}
