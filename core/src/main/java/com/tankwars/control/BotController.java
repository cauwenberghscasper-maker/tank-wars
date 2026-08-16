package com.tankwars.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tankwars.GameConfig;
import com.tankwars.model.Tank;
import com.tankwars.physics.Ballistics;
import com.tankwars.physics.ProjectileManager;

import java.util.Random;

/** Intentionally small, inaccurate MOVE/AIM/SHOOT controller. */
public final class BotController implements TankController {
    private final Tank tank;
    private final Tank target;
    private final ProjectileManager projectileManager;
    private final Random random;
    private final Vector2 pivot = new Vector2();
    private final Vector2 launchPoint = new Vector2();
    private final Vector2 targetPoint = new Vector2();
    private float movementTimer;
    private float shotTimer;
    private float movementDirection;
    private boolean charging;
    private float chargePower;
    private float plannedPower;
    private float plannedAngle;

    public BotController(Tank tank, Tank target, ProjectileManager projectileManager) {
        this(tank, target, projectileManager, 0xB07L);
    }

    BotController(Tank tank, Tank target, ProjectileManager projectileManager, long randomSeed) {
        this.tank = tank;
        this.target = target;
        this.projectileManager = projectileManager;
        this.random = new Random(randomSeed);
        movementTimer = randomRange(0.7f, 1.5f);
        shotTimer = randomRange(1.1f, 2.1f);
        movementDirection = -1f;
        chargePower = GameConfig.MIN_SHOT_POWER;
    }

    @Override
    public void update(float fixedDelta) {
        updateMovement(fixedDelta);

        if (charging) {
            tank.setTurretAngle(plannedAngle);
            chargePower = Math.min(plannedPower, chargePower + GameConfig.CHARGE_RATE * fixedDelta);
            if (chargePower >= plannedPower) {
                projectileManager.fire(tank, chargePower);
                charging = false;
                chargePower = GameConfig.MIN_SHOT_POWER;
                shotTimer = randomRange(GameConfig.BOT_MIN_SHOT_DELAY, GameConfig.BOT_MAX_SHOT_DELAY);
            }
        } else {
            aimDirectlyAtTarget();
            shotTimer -= fixedDelta;
            if (shotTimer <= 0f) {
                prepareShot();
                charging = true;
            }
        }
    }

    private void updateMovement(float fixedDelta) {
        movementTimer -= fixedDelta;
        if (movementTimer <= 0f) {
            float roll = random.nextFloat();
            movementDirection = roll < 0.42f ? -1f : roll < 0.84f ? 1f : 0f;
            movementTimer = randomRange(0.7f, 2.2f);
        }
        tank.setMovementDirection(movementDirection);
    }

    private void aimDirectlyAtTarget() {
        tank.getTurretPivot(pivot);
        targetPoint.set(target.getPosition().x, target.getPosition().y + target.getHeight() * 0.55f);
        float angle = (float) Math.atan2(targetPoint.y - pivot.y, targetPoint.x - pivot.x);
        if (angle < 0f) {
            angle += MathUtils.PI2;
        }
        tank.setTurretAngle(angle > MathUtils.PI ? MathUtils.PI : angle);
    }

    private void prepareShot() {
        tank.getTurretPivot(pivot);
        targetPoint.set(target.getPosition().x, target.getPosition().y + target.getHeight() * 0.5f);
        float horizontalDistance = Math.abs(targetPoint.x - pivot.x);
        float idealPower = (float) Math.sqrt(horizontalDistance * -GameConfig.GRAVITY) * 1.12f;
        idealPower = MathUtils.clamp(idealPower, 770f, GameConfig.MAX_SHOT_POWER * 0.91f);

        float angle = targetPoint.x < pivot.x ? MathUtils.PI * 0.75f : MathUtils.PI * 0.25f;
        launchPoint.set(pivot);
        for (int iteration = 0; iteration < 3; iteration++) {
            float localAngle = Ballistics.solveLowArc(
                launchPoint, targetPoint, idealPower, -GameConfig.GRAVITY);
            if (Float.isNaN(localAngle)) {
                localAngle = MathUtils.PI * 0.25f;
            }
            angle = targetPoint.x < pivot.x ? MathUtils.PI - localAngle : localAngle;
            launchPoint.set(pivot).add(
                MathUtils.cos(angle) * (GameConfig.TURRET_LENGTH + GameConfig.PROJECTILE_RADIUS + 2f),
                MathUtils.sin(angle) * (GameConfig.TURRET_LENGTH + GameConfig.PROJECTILE_RADIUS + 2f));
        }

        float aimError = (float) Math.toRadians(
            randomRange(-GameConfig.BOT_AIM_ERROR_DEGREES, GameConfig.BOT_AIM_ERROR_DEGREES));
        plannedAngle = MathUtils.clamp(angle + aimError, 0f, MathUtils.PI);
        plannedPower = MathUtils.clamp(
            idealPower * (1f + randomRange(-GameConfig.BOT_POWER_ERROR_FRACTION,
                GameConfig.BOT_POWER_ERROR_FRACTION)),
            GameConfig.MIN_SHOT_POWER, GameConfig.MAX_SHOT_POWER);
    }

    private float randomRange(float minimum, float maximum) {
        return minimum + random.nextFloat() * (maximum - minimum);
    }

    @Override
    public void cancelActions() {
        tank.stop();
        charging = false;
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
