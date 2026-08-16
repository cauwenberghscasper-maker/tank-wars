package com.tankwars;

/** All tuneable Version 0.1 gameplay values live here. */
public final class GameConfig {
    public static final float WORLD_WIDTH = 1920f;
    public static final float WORLD_HEIGHT = 1080f;
    public static final float GROUND_Y = 120f;

    public static final float FIXED_TIME_STEP = 1f / 120f;
    public static final float MAX_FRAME_TIME = 0.25f;
    public static final float GRAVITY = -520f;

    public static final float TANK_WIDTH = 150f;
    public static final float TANK_HEIGHT = 76f;
    public static final float TANK_SPEED = 270f;
    public static final float TURRET_PIVOT_HEIGHT = 61f;
    public static final float TURRET_LENGTH = 112f;
    public static final int MAX_HEALTH = 100;
    public static final float PLAYER_SPAWN_X = 265f;
    public static final float BOT_SPAWN_X = WORLD_WIDTH - 265f;

    public static final float PROJECTILE_RADIUS = 10f;
    public static final int PROJECTILE_DAMAGE = 25;
    public static final float MIN_SHOT_POWER = 430f;
    public static final float MAX_SHOT_POWER = 1180f;
    public static final float CHARGE_RATE = 410f;

    public static final float COUNTDOWN_SECONDS = 3f;
    public static final float BOT_AIM_ERROR_DEGREES = 4f;
    public static final float BOT_POWER_ERROR_FRACTION = 0.04f;
    public static final float BOT_MIN_SHOT_DELAY = 1.8f;
    public static final float BOT_MAX_SHOT_DELAY = 3.4f;

    private GameConfig() {
    }
}
