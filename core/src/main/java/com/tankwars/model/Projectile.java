package com.tankwars.model;

import com.badlogic.gdx.math.Vector2;
import com.tankwars.GameConfig;

/** A deterministic, fixed-step cannon projectile. */
public final class Projectile {
    private final Vector2 position = new Vector2();
    private final Vector2 previousPosition = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Team sourceTeam;
    private boolean alive = true;

    public Projectile(Vector2 position, Vector2 velocity, Team sourceTeam) {
        this.position.set(position);
        this.previousPosition.set(position);
        this.velocity.set(velocity);
        this.sourceTeam = sourceTeam;
    }

    public void updateFixed(float fixedDelta) {
        previousPosition.set(position);
        velocity.y += GameConfig.GRAVITY * fixedDelta;
        position.mulAdd(velocity, fixedDelta);
    }

    public void destroy() {
        alive = false;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Team getSourceTeam() {
        return sourceTeam;
    }

    public boolean isAlive() {
        return alive;
    }
}
