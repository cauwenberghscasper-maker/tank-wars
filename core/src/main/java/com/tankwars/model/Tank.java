package com.tankwars.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tankwars.GameConfig;

/** Mutable tank state. It deliberately has no knowledge of keyboard, mouse, or AI. */
public final class Tank {
    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Team team;
    private final float width;
    private final float height;
    private final float movementSpeed;
    private final int maxHealth;
    private int health;
    private float turretAngle;

    public Tank(float x, float y, Team team, float initialTurretAngle) {
        this.position.set(x, y);
        this.team = team;
        this.width = GameConfig.TANK_WIDTH;
        this.height = GameConfig.TANK_HEIGHT;
        this.movementSpeed = GameConfig.TANK_SPEED;
        this.maxHealth = GameConfig.MAX_HEALTH;
        this.health = maxHealth;
        setTurretAngle(initialTurretAngle);
    }

    public void update(float delta, float worldWidth) {
        position.mulAdd(velocity, delta);
        float halfWidth = width * 0.5f;
        position.x = MathUtils.clamp(position.x, halfWidth, worldWidth - halfWidth);
    }

    public void setGroundHeight(float groundHeight) {
        position.y = groundHeight;
    }

    public void setMovementDirection(float direction) {
        velocity.set(MathUtils.clamp(direction, -1f, 1f) * movementSpeed, 0f);
    }

    public void stop() {
        velocity.setZero();
    }

    public void setTurretAngle(float angleRadians) {
        turretAngle = MathUtils.clamp(angleRadians, 0f, MathUtils.PI);
    }

    public void takeDamage(int damage) {
        if (damage > 0) {
            health = Math.max(0, health - damage);
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public Vector2 getTurretPivot(Vector2 out) {
        return out.set(position.x, position.y + GameConfig.TURRET_PIVOT_HEIGHT);
    }

    public Vector2 getMuzzlePosition(Vector2 out) {
        getTurretPivot(out);
        return out.add(MathUtils.cos(turretAngle) * GameConfig.TURRET_LENGTH,
            MathUtils.sin(turretAngle) * GameConfig.TURRET_LENGTH);
    }

    public Rectangle getBounds(Rectangle out) {
        return out.set(position.x - width * 0.5f, position.y, width, height);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Team getTeam() {
        return team;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getTurretAngle() {
        return turretAngle;
    }
}
