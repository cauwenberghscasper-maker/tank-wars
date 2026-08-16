package com.tankwars.control;

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
    void releaseAndRepressBetweenStepsFiresThenStartsNewCharge() {
        Fixture fixture = new Fixture();
        fixture.input.press();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);
        assertTrue(fixture.controller.isCharging());

        fixture.input.release();
        fixture.input.press();
        fixture.controller.update(GameConfig.FIXED_TIME_STEP);

        assertEquals(1, fixture.projectiles.getProjectiles().size);
        assertTrue(fixture.controller.isCharging());
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

    private static final class Fixture {
        private final FakePlayerInput input = new FakePlayerInput();
        private final ProjectileManager projectiles = new ProjectileManager();
        private final PlayerController controller = new PlayerController(
            new Tank(GameConfig.PLAYER_SPAWN_X, Team.PLAYER, 0f), input, projectiles);
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
