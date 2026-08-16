package com.tankwars.control;

import com.badlogic.gdx.math.Vector2;
import com.tankwars.GameConfig;
import com.tankwars.input.PlayerInput;
import com.tankwars.model.Tank;
import com.tankwars.physics.AimMath;
import com.tankwars.physics.ProjectileManager;

public final class PlayerController implements TankController {
    private final Tank tank;
    private final PlayerInput input;
    private final ProjectileManager projectileManager;
    private final Vector2 turretPivot = new Vector2();
    private boolean wasFirePressed;
    private boolean charging;
    private float chargePower = GameConfig.MIN_SHOT_POWER;

    public PlayerController(Tank tank, PlayerInput input, ProjectileManager projectileManager) {
        this.tank = tank;
        this.input = input;
        this.projectileManager = projectileManager;
    }

    @Override
    public void update(float fixedDelta) {
        updateAim();
        tank.setMovementDirection(input.getMoveAxis());

        boolean firePressed = input.isFirePressed();
        if (firePressed) {
            charging = true;
            chargePower = Math.min(GameConfig.MAX_SHOT_POWER,
                chargePower + GameConfig.CHARGE_RATE * fixedDelta);
        } else if (wasFirePressed && charging) {
            projectileManager.fire(tank, chargePower);
            charging = false;
            chargePower = GameConfig.MIN_SHOT_POWER;
        }
        wasFirePressed = firePressed;
    }

    public void updateAim() {
        tank.getTurretPivot(turretPivot);
        tank.setTurretAngle(AimMath.upperHemisphereAngle(turretPivot, input.getAimWorldPosition()));
    }

    @Override
    public void cancelActions() {
        tank.stop();
        charging = false;
        wasFirePressed = input.isFirePressed();
        chargePower = GameConfig.MIN_SHOT_POWER;
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
}
