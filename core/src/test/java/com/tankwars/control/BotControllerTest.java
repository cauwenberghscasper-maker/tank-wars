package com.tankwars.control;

import com.badlogic.gdx.math.MathUtils;
import com.tankwars.GameConfig;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import com.tankwars.physics.ProjectileManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BotControllerTest {
    @Test
    void botCanEventuallyKillStationaryPlayer() {
        Tank player = new Tank(GameConfig.PLAYER_SPAWN_X, Team.PLAYER, MathUtils.PI * 0.25f);
        Tank bot = new Tank(GameConfig.BOT_SPAWN_X, Team.BOT, MathUtils.PI * 0.75f);
        ProjectileManager projectiles = new ProjectileManager();
        BotController controller = new BotController(bot, player, projectiles, 0xB07L);

        int maximumSteps = (int) (180f / GameConfig.FIXED_TIME_STEP);
        for (int step = 0; step < maximumSteps && player.isAlive(); step++) {
            controller.update(GameConfig.FIXED_TIME_STEP);
            bot.update(GameConfig.FIXED_TIME_STEP);
            projectiles.updateFixed(GameConfig.FIXED_TIME_STEP, player, bot);
        }

        assertFalse(player.isAlive(), "the prototype bot should be capable of winning");
    }
}
