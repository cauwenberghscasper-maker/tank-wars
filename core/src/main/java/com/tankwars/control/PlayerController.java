package com.tankwars.control;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.tankwars.GameConfig;
import com.tankwars.input.FireInputEvent;
import com.tankwars.input.PlayerInput;
import com.tankwars.model.Tank;
import com.tankwars.physics.AimMath;
import com.tankwars.physics.ProjectileManager;

public final class PlayerController implements TankController {
    private final Tank tank;
    private final PlayerInput input;
    private final ProjectileManager projectileManager;
    private final Vector2 turretPivot = new Vector2();
    private boolean charging;
    private float chargePower = GameConfig.MIN_SHOT_POWER;
    private float cooldownRemaining;

    public PlayerController(Tank tank, PlayerInput input, ProjectileManager projectileManager) {
        this.tank = tank;
        this.input = input;
        this.projectileManager = projectileManager;
    }

    @Override
    public void update(float fixedDelta) {
        cooldownRemaining = Math.max(0f, cooldownRemaining - fixedDelta);
        updateAim(fixedDelta);
        tank.setMovementDirection(input.getMoveAxis());

        FireInputEvent event;
        while ((event = input.pollFireEvent()) != FireInputEvent.NONE) {
            if (event == FireInputEvent.PRESS && !charging && cooldownRemaining <= 0f) {
                beginCharge();
            } else if (event == FireInputEvent.RELEASE && charging) {
                fireChargedShot();
            } else if (event == FireInputEvent.CANCEL) {
                cancelCharge();
            }
        }

        if (charging && input.isFireHeld()) {
            chargePower = Math.min(GameConfig.MAX_SHOT_POWER,
                chargePower + GameConfig.CHARGE_RATE * fixedDelta);
        } else if (charging) {
            // Fallback when the desktop loses a release callback but its level state recovered.
            fireChargedShot();
        }
    }

    private void beginCharge() {
        charging = true;
        chargePower = GameConfig.MIN_SHOT_POWER;
    }

    private void fireChargedShot() {
        projectileManager.fire(tank, chargePower);
        cooldownRemaining = GameConfig.PLAYER_SHOT_COOLDOWN_SECONDS;
        cancelCharge();
    }

    private void cancelCharge() {
        charging = false;
        chargePower = GameConfig.MIN_SHOT_POWER;
    }

    public void updateAim(float fixedDelta) {
        tank.getTurretPivot(turretPivot);
        float targetAngle = AimMath.upperHemisphereAngle(turretPivot, input.getAimWorldPosition());
        float maximumTurn = MathUtils.degreesToRadians
            * GameConfig.PLAYER_TURRET_TURN_SPEED_DEGREES * fixedDelta;
        tank.setTurretAngle(AimMath.moveTowardUpperHemisphere(
            tank.getTurretAngle(), targetAngle, maximumTurn));
    }

    @Override
    public void cancelActions() {
        tank.stop();
        cancelCharge();
    }

    public boolean isCharging() {
        return charging;
    }

    public float getChargePercent() {
        return charging
            ? (chargePower - GameConfig.MIN_SHOT_POWER)
                / (GameConfig.MAX_SHOT_POWER - GameConfig.MIN_SHOT_POWER)
            : 0f;
    }

    public boolean isCoolingDown() {
        return cooldownRemaining > 0f;
    }

    public float getCooldownRemaining() {
        return cooldownRemaining;
    }

    public float getCooldownProgress() {
        return 1f - cooldownRemaining / GameConfig.PLAYER_SHOT_COOLDOWN_SECONDS;
    }
}
