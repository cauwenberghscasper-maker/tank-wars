package com.tankwars.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tankwars.GameConfig;
import com.tankwars.model.Projectile;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;
import com.tankwars.world.Terrain;

public final class ProjectileManager {
    private final Array<Projectile> projectiles = new Array<Projectile>();
    private final Vector2 temporaryPosition = new Vector2();
    private final Vector2 temporaryVelocity = new Vector2();
    private final Rectangle temporaryBounds = new Rectangle();

    public void fire(Tank source, float power) {
        float angle = source.getTurretAngle();
        source.getMuzzlePosition(temporaryPosition);
        temporaryPosition.add(
            MathUtils.cos(angle) * (GameConfig.PROJECTILE_RADIUS + 2f),
            MathUtils.sin(angle) * (GameConfig.PROJECTILE_RADIUS + 2f));
        temporaryVelocity.set(MathUtils.cos(angle), MathUtils.sin(angle)).scl(power);
        projectiles.add(new Projectile(temporaryPosition, temporaryVelocity, source.getTeam()));
    }

    public void updateFixed(
        float fixedDelta, Tank player, Tank bot, Terrain terrain, float worldHeight) {
        for (int index = projectiles.size - 1; index >= 0; index--) {
            Projectile projectile = projectiles.get(index);
            projectile.updateFixed(fixedDelta);

            Tank target = projectile.getSourceTeam() == Team.PLAYER ? bot : player;
            if (target.isAlive()
                && CollisionMath.sweptCircleIntersectsRectangle(
                    projectile.getPreviousPosition(), projectile.getPosition(),
                    GameConfig.PROJECTILE_RADIUS, target.getBounds(temporaryBounds))) {
                target.takeDamage(GameConfig.PROJECTILE_DAMAGE);
                projectile.destroy();
            } else if (hitGround(projectile, terrain)
                || leftPlayableWorld(projectile, terrain.getWidth(), worldHeight)) {
                projectile.destroy();
            }

            if (!projectile.isAlive()) {
                projectiles.removeIndex(index);
            }
        }
    }

    private boolean hitGround(Projectile projectile, Terrain terrain) {
        return projectile.getPosition().y - GameConfig.PROJECTILE_RADIUS
            <= terrain.getHeightAt(projectile.getPosition().x);
    }

    private boolean leftPlayableWorld(Projectile projectile, float worldWidth, float worldHeight) {
        Vector2 position = projectile.getPosition();
        return position.x < -100f || position.x > worldWidth + 100f
            || position.y > worldHeight + 300f;
    }

    public Array<Projectile> getProjectiles() {
        return projectiles;
    }

    public void clear() {
        projectiles.clear();
    }
}
