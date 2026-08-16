package com.tankwars;

/** All tuneable Version 0.1 gameplay values live here. */
public final class GameConfig {
    public static final float VIEW_WIDTH = 1920f;
    public static final float VIEW_HEIGHT = 1080f;

    public static final float FIXED_TIME_STEP = 1f / 120f;
    public static final float MAX_FRAME_TIME = 0.25f;
    public static final float GRAVITY = -520f;

    public static final float TANK_WIDTH = 150f;
    public static final float TANK_HEIGHT = 76f;
    public static final float TANK_SPEED = 270f;
    public static final float TURRET_PIVOT_HEIGHT = 61f;
    public static final float TURRET_LENGTH = 112f;
    public static final float PLAYER_TURRET_TURN_SPEED_DEGREES = 300f;
    public static final int MAX_HEALTH = 100;

    public static final float PROJECTILE_RADIUS = 10f;
    public static final int PROJECTILE_DAMAGE = 25;
    public static final float MIN_SHOT_POWER = 500f;
    public static final float MAX_SHOT_POWER = 1450f;
    public static final float CHARGE_RATE = 475f;

    public static final float COUNTDOWN_SECONDS = 3f;
    public static final float BOT_AIM_ERROR_DEGREES = 4f;
    public static final float BOT_POWER_ERROR_FRACTION = 0.04f;
    public static final float BOT_MIN_SHOT_DELAY = 1.8f;
    public static final float BOT_MAX_SHOT_DELAY = 3.4f;
    public static final float TURN_DURATION_SECONDS = 25f;
    public static final float TURN_TRANSITION_SECONDS = 0.8f;
    public static final float CAMERA_FOLLOW_SPEED = 5f;

    public static final float EXIT_BUTTON_X = VIEW_WIDTH * 0.5f - 105f;
    public static final float EXIT_BUTTON_Y = VIEW_HEIGHT - 88f;
    public static final float EXIT_BUTTON_WIDTH = 210f;
    public static final float EXIT_BUTTON_HEIGHT = 58f;

    private GameConfig() {
    }
}
