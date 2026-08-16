package com.tankwars;

import com.badlogic.gdx.math.Vector2;
import com.tankwars.input.FireInputEvent;
import com.tankwars.input.PlayerInput;
import com.tankwars.maps.MapDefinition;
import com.tankwars.maps.SpawnPoint;
import com.tankwars.maps.TerrainProfileDefinition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GameWorldMapTest {
    @Test
    void selectedMapCreatesTerrainAndTankSpawns() {
        MapDefinition map = new MapDefinition(
            "test-map",
            "Test Map",
            2400f,
            1200f,
            "test-theme",
            new TerrainProfileDefinition(1200f, new float[] { 90f, 90f, 90f }),
            Arrays.asList(
                new SpawnPoint("alpha", 300f, 0.5f),
                new SpawnPoint("bravo", 2100f, 2.5f)));

        GameWorld world = new GameWorld(new IdleInput(), map);

        assertSame(map, world.getMapDefinition());
        assertEquals(2400f, world.getTerrain().getWidth(), 0.001f);
        assertEquals(300f, world.getPlayerTank().getPosition().x, 0.001f);
        assertEquals(90f, world.getPlayerTank().getPosition().y, 0.001f);
        assertEquals(0.5f, world.getPlayerTank().getTurretAngle(), 0.001f);
        assertEquals(2100f, world.getBotTank().getPosition().x, 0.001f);
        assertEquals(90f, world.getBotTank().getPosition().y, 0.001f);
        assertEquals(2.5f, world.getBotTank().getTurretAngle(), 0.001f);
    }

    private static final class IdleInput implements PlayerInput {
        private final Vector2 aim = new Vector2();

        @Override public float getMoveAxis() { return 0f; }
        @Override public Vector2 getAimWorldPosition() { return aim; }
        @Override public FireInputEvent pollFireEvent() { return FireInputEvent.NONE; }
        @Override public boolean isFireHeld() { return false; }
        @Override public boolean isRestartJustPressed() { return false; }
        @Override public boolean consumeExitRequested() { return false; }
        @Override public void cancelActiveGestures() { }
    }
}
