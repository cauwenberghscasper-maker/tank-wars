package com.tankwars.control;

import com.badlogic.gdx.math.MathUtils;
import com.tankwars.GameConfig;
import com.tankwars.maps.BuiltInMaps;
import com.tankwars.maps.MapDefinition;
import com.tankwars.maps.SpawnPoint;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import com.tankwars.physics.ProjectileManager;
import com.tankwars.world.Terrain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BotControllerTest {
    @Test
    void botCanEventuallyKillStationaryPlayer() {
        MapDefinition map = BuiltInMaps.grasslands();
        Terrain terrain = new Terrain(map.getWidth(), map.getTerrainProfile());
        SpawnPoint playerSpawn = map.getSpawnPoints().get(0);
        SpawnPoint botSpawn = map.getSpawnPoints().get(1);
        Tank player = new Tank(playerSpawn.getX(), terrain.getHeightAt(playerSpawn.getX()),
            Team.PLAYER, MathUtils.PI * 0.25f);
        Tank bot = new Tank(botSpawn.getX(), terrain.getHeightAt(botSpawn.getX()),
            Team.BOT, MathUtils.PI * 0.75f);
        ProjectileManager projectiles = new ProjectileManager();
        BotController controller = new BotController(bot, player, projectiles, 0xB07L);
        boolean shotInFlight = false;

        int maximumSteps = (int) (180f / GameConfig.FIXED_TIME_STEP);
        for (int step = 0; step < maximumSteps && player.isAlive(); step++) {
            if (!shotInFlight) {
                controller.update(GameConfig.FIXED_TIME_STEP);
            }
            bot.update(GameConfig.FIXED_TIME_STEP, map.getWidth());
            projectiles.updateFixed(
                GameConfig.FIXED_TIME_STEP, player, bot, terrain, map.getHeight());
            if (controller.consumeShotFiredEvent()) {
                shotInFlight = true;
            }
            if (shotInFlight && projectiles.getProjectiles().isEmpty()) {
                controller.beginTurn();
                shotInFlight = false;
            }
        }

        assertFalse(player.isAlive(), "the prototype bot should be capable of winning");
    }
}
