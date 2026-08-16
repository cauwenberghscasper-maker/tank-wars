package com.tankwars.physics;

import com.tankwars.GameConfig;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectileManagerTest {
    @Test
    void projectileDamagesOnlyOpposingTankAndIsRemoved() {
        Tank player = new Tank(300f, Team.PLAYER, 0f);
        Tank bot = new Tank(550f, Team.BOT, 0f);
        ProjectileManager projectiles = new ProjectileManager();
        projectiles.fire(player, 900f);

        for (int step = 0; step < 120 && projectiles.getProjectiles().size > 0; step++) {
            projectiles.updateFixed(GameConfig.FIXED_TIME_STEP, player, bot);
        }

        assertEquals(GameConfig.MAX_HEALTH, player.getHealth());
        assertEquals(GameConfig.MAX_HEALTH - GameConfig.PROJECTILE_DAMAGE, bot.getHealth());
        assertTrue(projectiles.getProjectiles().isEmpty());
    }
}
