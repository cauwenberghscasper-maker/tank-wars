package com.tankwars.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tankwars.GameConfig;
import com.tankwars.input.FireInputEvent;
import com.tankwars.input.PlayerInput;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import com.tankwars.physics.ProjectileManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerControllerTest {
    @Test
    void quickTapBetweenSimulationStepsStillFires() {
        Fixture fixture = new Fixture();
        fixture.input.press();
        fixture.input.release();

        fixture.controller.update(GameConfig.FIXED_TIME_STEP);

        assertEquals(1, fixture.projectiles.getProjectiles().size);
        assertFalse(fixture.controller.isCharging());
    }

    @Test
    void releaseAndRepressBetweenStepsFiresButTurnLockBlocksNewCharge() {
        Fixture fixture = new Fixture();
        fixture.input.press();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);
        assertTrue(fixture.controller.isCharging());

        fixture.input.release();
        fixture.input.press();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);

        assertEquals(1, fixture.projectiles.getProjectiles().size);
        assertFalse(fixture.controller.isCharging());
        assertTrue(fixture.controller.hasFiredThisTurn());
    }

    @Test
    void cancelledGestureClearsChargeWithoutShooting() {
        Fixture fixture = new Fixture();
        fixture.input.press();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);

        fixture.input.cancelActiveGestures();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);

        assertTrue(fixture.projectiles.getProjectiles().isEmpty());
        assertFalse(fixture.controller.isCharging());
    }

    @Test
    void onlyOneShotIsAllowedUntilANewTurnBegins() {
        Fixture fixture = new Fixture();
        fixture.input.press();
        fixture.input.release();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);

        fixture.input.press();
        fixture.input.release();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);
        assertEquals(1, fixture.projectiles.getProjectiles().size);

        fixture.controller.beginTurn();
        fixture.input.press();
        fixture.input.release();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);
        assertEquals(2, fixture.projectiles.getProjectiles().size);
    }

    @Test
    void smoothedAimIsFrameRateIndependent() {
        Fixture oneStep = new Fixture();
        Fixture manySteps = new Fixture();
        oneStep.input.aim.set(
            0f, oneStep.tank.getPosition().y + GameConfig.TURRET_PIVOT_HEIGHT);
        manySteps.input.aim.set(oneStep.input.aim);

        oneStep.controller.update(0.2f);
        for (int step = 0; step < 20; step++) {
            manySteps.controller.update(0.01f);
        }

        assertEquals(oneStep.tank.getTurretAngle(), manySteps.tank.getTurretAngle(), 0.001f);
        assertEquals(60f, oneStep.tank.getTurretAngle() * MathUtils.radiansToDegrees, 0.01f);
    }

    private static final class Fixture {
        private final FakePlayerInput input = new FakePlayerInput();
        private final ProjectileManager projectiles = new ProjectileManager();
        private final Tank tank = new Tank(430f, 170f, Team.PLAYER, 0f);
        private final PlayerController controller = new PlayerController(
            tank, input, projectiles);
    }

    private static final class FakePlayerInput implements PlayerInput {
        private final Deque<FireInputEvent> events = new ArrayDeque<FireInputEvent>();
        private final Vector2 aim = new Vector2(1000f, 500f);
        private boolean held;

        private void press() {
            held = true;
            events.addLast(FireInputEvent.PRESS);
        }

        private void release() {
            held = false;
            events.addLast(FireInputEvent.RELEASE);
        }

        @Override
        public float getMoveAxis() {
            return 0f;
        }

        @Override
        public Vector2 getAimWorldPosition() {
            return aim;
        }

        @Override
        public FireInputEvent pollFireEvent() {
            FireInputEvent event = events.pollFirst();
            return event == null ? FireInputEvent.NONE : event;
        }

        @Override
        public boolean isFireHeld() {
            return held;
        }

        @Override
        public boolean isRestartJustPressed() {
            return false;
        }

        @Override
        public boolean consumeExitRequested() {
            return false;
        }

        @Override
        public void cancelActiveGestures() {
            held = false;
            events.clear();
            events.addLast(FireInputEvent.CANCEL);
        }
    }
}
