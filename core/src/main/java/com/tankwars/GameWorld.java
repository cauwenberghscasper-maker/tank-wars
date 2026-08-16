package com.tankwars;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tankwars.control.BotController;
import com.tankwars.control.PlayerController;
import com.tankwars.input.PlayerInput;
import com.tankwars.maps.BuiltInMaps;
import com.tankwars.maps.MapDefinition;
import com.tankwars.maps.SpawnPoint;
import com.tankwars.model.Projectile;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import com.tankwars.physics.ProjectileManager;
import com.tankwars.world.Terrain;

/** Owns one deterministic, alternating-turn match. */
public final class GameWorld {
    private final PlayerInput input;
    private final MapDefinition mapDefinition;
    private final ProjectileManager projectileManager = new ProjectileManager();
    private Terrain terrain;
    private Tank playerTank;
    private Tank botTank;
    private PlayerController playerController;
    private BotController botController;
    private MatchState matchState;
    private TurnPhase turnPhase;
    private Team activeTurn;
    private Team winner;
    private float countdownRemaining;
    private float turnRemaining;
    private float transitionRemaining;

    public GameWorld(PlayerInput input) {
        this(input, BuiltInMaps.grasslands());
    }

    public GameWorld(PlayerInput input, MapDefinition mapDefinition) {
        if (input == null) {
            throw new IllegalArgumentException("Player input is required");
        }
        if (mapDefinition == null || mapDefinition.getSpawnPoints().size() < 2) {
            throw new IllegalArgumentException("The prototype match requires a map with two spawns");
        }
        this.input = input;
        this.mapDefinition = mapDefinition;
        reset();
    }

    public void updateFixed(float fixedDelta) {
        if (matchState == MatchState.COUNTDOWN) {
            playerController.updateAim(fixedDelta);
            countdownRemaining -= fixedDelta;
            if (countdownRemaining <= 0f) {
                countdownRemaining = 0f;
                matchState = MatchState.PLAYING;
                startTurn(Team.PLAYER);
            }
            return;
        }
        if (matchState == MatchState.GAME_OVER) {
            if (input.isRestartJustPressed()) {
                reset();
            }
            return;
        }

        if (turnPhase == TurnPhase.CONTROL) {
            updateControlPhase(fixedDelta);
        } else if (turnPhase == TurnPhase.PROJECTILE_IN_FLIGHT) {
            playerTank.stop();
            botTank.stop();
            projectileManager.updateFixed(
                fixedDelta, playerTank, botTank, terrain, mapDefinition.getHeight());
            if (projectileManager.getProjectiles().isEmpty()) {
                beginTransition();
            }
        } else {
            transitionRemaining -= fixedDelta;
            if (transitionRemaining <= 0f) {
                startTurn(activeTurn == Team.PLAYER ? Team.BOT : Team.PLAYER);
            }
        }

        if (!playerTank.isAlive()) {
            finishMatch(Team.BOT);
        } else if (!botTank.isAlive()) {
            finishMatch(Team.PLAYER);
        }
    }

    private void updateControlPhase(float fixedDelta) {
        turnRemaining = Math.max(0f, turnRemaining - fixedDelta);
        if (activeTurn == Team.PLAYER) {
            playerController.update(fixedDelta);
            botTank.stop();
        } else {
            botController.update(fixedDelta);
            playerTank.stop();
        }
        playerTank.update(fixedDelta, mapDefinition.getWidth());
        botTank.update(fixedDelta, mapDefinition.getWidth());
        snapTanksToTerrain();

        boolean fired = activeTurn == Team.PLAYER
            ? playerController.consumeShotFiredEvent()
            : botController.consumeShotFiredEvent();
        if (fired) {
            turnPhase = TurnPhase.PROJECTILE_IN_FLIGHT;
            playerTank.stop();
            botTank.stop();
        } else if (turnRemaining <= 0f) {
            beginTransition();
        }
    }

    private void startTurn(Team team) {
        activeTurn = team;
        turnPhase = TurnPhase.CONTROL;
        turnRemaining = GameConfig.TURN_DURATION_SECONDS;
        input.cancelActiveGestures();
        playerController.cancelActions();
        botController.cancelActions();
        if (team == Team.PLAYER) {
            playerController.beginTurn();
        } else {
            botController.beginTurn();
        }
    }

    private void beginTransition() {
        turnPhase = TurnPhase.TRANSITION;
        transitionRemaining = GameConfig.TURN_TRANSITION_SECONDS;
        input.cancelActiveGestures();
        playerController.cancelActions();
        botController.cancelActions();
    }

    private void finishMatch(Team winningTeam) {
        winner = winningTeam;
        matchState = MatchState.GAME_OVER;
        input.cancelActiveGestures();
        playerController.cancelActions();
        botController.cancelActions();
        projectileManager.clear();
    }

    private void snapTanksToTerrain() {
        playerTank.setGroundHeight(terrain.getHeightAt(playerTank.getPosition().x));
        botTank.setGroundHeight(terrain.getHeightAt(botTank.getPosition().x));
    }

    public void cancelPlayerActions() {
        input.cancelActiveGestures();
        playerController.cancelActions();
    }

    public void reset() {
        input.cancelActiveGestures();
        projectileManager.clear();
        terrain = new Terrain(mapDefinition.getWidth(), mapDefinition.getTerrainProfile());
        SpawnPoint playerSpawn = mapDefinition.getSpawnPoints().get(0);
        SpawnPoint botSpawn = mapDefinition.getSpawnPoints().get(1);
        playerTank = createTank(playerSpawn, Team.PLAYER);
        botTank = createTank(botSpawn, Team.BOT);
        playerController = new PlayerController(playerTank, input, projectileManager);
        botController = new BotController(botTank, playerTank, projectileManager);
        matchState = MatchState.COUNTDOWN;
        turnPhase = TurnPhase.CONTROL;
        activeTurn = Team.PLAYER;
        winner = null;
        countdownRemaining = GameConfig.COUNTDOWN_SECONDS;
        turnRemaining = GameConfig.TURN_DURATION_SECONDS;
    }

    private Tank createTank(SpawnPoint spawn, Team team) {
        float groundHeight = terrain.getHeightAt(spawn.getX());
        return new Tank(
            spawn.getX(), groundHeight, team, spawn.getTurretAngleRadians());
    }

    public Vector2 getCameraTarget(Vector2 out) {
        if (!projectileManager.getProjectiles().isEmpty()) {
            Projectile projectile = projectileManager.getProjectiles().peek();
            return out.set(projectile.getPosition());
        }
        Tank focusTank = activeTurn == Team.PLAYER ? playerTank : botTank;
        return out.set(focusTank.getPosition().x, GameConfig.VIEW_HEIGHT * 0.5f);
    }

    public Tank getPlayerTank() { return playerTank; }
    public Tank getBotTank() { return botTank; }
    public ProjectileManager getProjectileManager() { return projectileManager; }
    public PlayerController getPlayerController() { return playerController; }
    public BotController getBotController() { return botController; }
    public MapDefinition getMapDefinition() { return mapDefinition; }
    public Terrain getTerrain() { return terrain; }
    public MatchState getMatchState() { return matchState; }
    public TurnPhase getTurnPhase() { return turnPhase; }
    public Team getActiveTurn() { return activeTurn; }
    public Team getWinner() { return winner; }
    public float getTurnRemaining() { return turnRemaining; }
    public int getCountdownNumber() { return MathUtils.ceil(countdownRemaining); }
}
