package com.tankwars;

import com.badlogic.gdx.math.Vector2;
import com.tankwars.input.FireInputEvent;
import com.tankwars.input.PlayerInput;
import com.tankwars.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameWorldTurnTest {
    @Test
    void playerShotFullyResolvesBeforeBotControlBegins() {
        FakeInput input = new FakeInput();
        GameWorld world = new GameWorld(input);
        advance(world, GameConfig.COUNTDOWN_SECONDS + GameConfig.FIXED_TIME_STEP);
        assertEquals(Team.PLAYER, world.getActiveTurn());
        assertEquals(TurnPhase.CONTROL, world.getTurnPhase());

        input.pressAndRelease();
        world.updateFixed(GameConfig.FIXED_TIME_STEP);
        assertEquals(TurnPhase.PROJECTILE_IN_FLIGHT, world.getTurnPhase());

        int maximumSteps = (int) (12f / GameConfig.FIXED_TIME_STEP);
        for (int step = 0; step < maximumSteps
            && !(world.getActiveTurn() == Team.BOT && world.getTurnPhase() == TurnPhase.CONTROL);
            step++) {
            world.updateFixed(GameConfig.FIXED_TIME_STEP);
        }
        assertEquals(Team.BOT, world.getActiveTurn());
        assertEquals(TurnPhase.CONTROL, world.getTurnPhase());
    }

    private void advance(GameWorld world, float seconds) {
        int steps = (int) Math.ceil(seconds / GameConfig.FIXED_TIME_STEP);
        for (int step = 0; step < steps; step++) {
            world.updateFixed(GameConfig.FIXED_TIME_STEP);
        }
    }

    private static final class FakeInput implements PlayerInput {
        private final Deque<FireInputEvent> events = new ArrayDeque<FireInputEvent>();
        private final Vector2 aim = new Vector2(1150f, 650f);

        private void pressAndRelease() {
            events.addLast(FireInputEvent.PRESS);
            events.addLast(FireInputEvent.RELEASE);
        }

        @Override public float getMoveAxis() { return 0f; }
        @Override public Vector2 getAimWorldPosition() { return aim; }
        @Override public FireInputEvent pollFireEvent() {
            FireInputEvent event = events.pollFirst();
            return event == null ? FireInputEvent.NONE : event;
        }
        @Override public boolean isFireHeld() { return false; }
        @Override public boolean isRestartJustPressed() { return false; }
        @Override public boolean consumeExitRequested() { return false; }
        @Override public void cancelActiveGestures() { events.clear(); }
    }
}
