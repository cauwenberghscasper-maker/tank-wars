package com.tankwars;

import com.badlogic.gdx.math.MathUtils;
import com.tankwars.control.BotController;
import com.tankwars.control.PlayerController;
import com.tankwars.input.PlayerInput;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import com.tankwars.physics.ProjectileManager;

/** Owns a single match and advances it only through fixed simulation steps. */
public final class GameWorld {
    private final PlayerInput input;
    private final ProjectileManager projectileManager = new ProjectileManager();
    private Tank playerTank;
    private Tank botTank;
    private PlayerController playerController;
    private BotController botController;
    private MatchState matchState;
    private Team winner;
    private float countdownRemaining;

    public GameWorld(PlayerInput input) {
        this.input = input;
        reset();
    }

    public void updateFixed(float fixedDelta) {
        if (matchState == MatchState.COUNTDOWN) {
            playerController.updateAim(fixedDelta);
            countdownRemaining -= fixedDelta;
            if (countdownRemaining <= 0f) {
                countdownRemaining = 0f;
                playerController.cancelActions();
                matchState = MatchState.PLAYING;
            }
            return;
        }

        if (matchState == MatchState.GAME_OVER) {
            if (input.isRestartJustPressed()) {
                reset();
            }
            return;
        }

        playerController.update(fixedDelta);
        botController.update(fixedDelta);
        playerTank.update(fixedDelta);
        botTank.update(fixedDelta);
        projectileManager.updateFixed(fixedDelta, playerTank, botTank);

        if (!playerTank.isAlive()) {
            finishMatch(Team.BOT);
        } else if (!botTank.isAlive()) {
            finishMatch(Team.PLAYER);
        }
    }

    private void finishMatch(Team winningTeam) {
        winner = winningTeam;
        matchState = MatchState.GAME_OVER;
        playerController.cancelActions();
        botController.cancelActions();
        projectileManager.clear();
    }

    public void cancelPlayerActions() {
        input.cancelActiveGestures();
        playerController.cancelActions();
    }

    public void reset() {
        input.cancelActiveGestures();
        projectileManager.clear();
        playerTank = new Tank(GameConfig.PLAYER_SPAWN_X, Team.PLAYER, MathUtils.PI * 0.25f);
        botTank = new Tank(GameConfig.BOT_SPAWN_X, Team.BOT, MathUtils.PI * 0.75f);
        playerController = new PlayerController(playerTank, input, projectileManager);
        botController = new BotController(botTank, playerTank, projectileManager);
        matchState = MatchState.COUNTDOWN;
        winner = null;
        countdownRemaining = GameConfig.COUNTDOWN_SECONDS;
    }

    public Tank getPlayerTank() {
        return playerTank;
    }

    public Tank getBotTank() {
        return botTank;
    }

    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public BotController getBotController() {
        return botController;
    }

    public MatchState getMatchState() {
        return matchState;
    }

    public Team getWinner() {
        return winner;
    }

    public int getCountdownNumber() {
        return MathUtils.ceil(countdownRemaining);
    }
}
