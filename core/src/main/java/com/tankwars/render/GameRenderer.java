package com.tankwars.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tankwars.GameConfig;
import com.tankwars.GameWorld;
import com.tankwars.MatchState;
import com.tankwars.TurnPhase;
import com.tankwars.assets.GameAssets;
import com.tankwars.assets.EnvironmentTheme;
import com.tankwars.model.Projectile;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;

/** World and HUD renderer. Environment art is replaceable through {@link GameAssets}. */
public final class GameRenderer {
    private static final Color PLAYER = new Color(0.10f, 0.55f, 0.72f, 1f);
    private static final Color BOT = new Color(0.79f, 0.27f, 0.20f, 1f);
    private static final Color DARK = new Color(0.035f, 0.05f, 0.07f, 1f);
    private static final Color TRACK = new Color(0.08f, 0.13f, 0.16f, 1f);
    private static final Color WHEEL = new Color(0.32f, 0.40f, 0.42f, 1f);
    private static final Color METAL_HIGHLIGHT = new Color(0.63f, 0.77f, 0.78f, 1f);
    private static final Color PROJECTILE = new Color(1f, 0.78f, 0.22f, 1f);
    private static final Color PANEL = new Color(0.11f, 0.15f, 0.21f, 0.94f);

    private final OrthographicCamera worldCamera;
    private final OrthographicCamera hudCamera;
    private final GameAssets assets;
    private final EnvironmentTheme theme;
    private final EnvironmentRenderer environmentRenderer;
    private final TerrainRenderer terrainRenderer;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final Vector2 pivot = new Vector2();
    private final Vector2 muzzle = new Vector2();

    public GameRenderer(
        OrthographicCamera worldCamera,
        OrthographicCamera hudCamera,
        GameAssets assets,
        EnvironmentTheme theme) {
        this.worldCamera = worldCamera;
        this.hudCamera = hudCamera;
        this.assets = assets;
        this.theme = theme;
        this.environmentRenderer = new EnvironmentRenderer(assets);
        this.terrainRenderer = new TerrainRenderer(theme);
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
    }

    public void render(GameWorld world) {
        Color clear = theme.getClearColor();
        Gdx.gl.glClearColor(clear.r, clear.g, clear.b, clear.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(worldCamera.combined);
        environmentRenderer.render(batch, worldCamera);
        drawWorldShapes(world);
        drawHudShapes(world);
        drawHudText(world);
    }

    private void drawWorldShapes(GameWorld world) {
        shapes.setProjectionMatrix(worldCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        terrainRenderer.drawFilled(
            shapes, world.getTerrain(), world.getMapDefinition().getWidth());
        drawTank(world.getPlayerTank(), PLAYER);
        drawTank(world.getBotTank(), BOT);
        for (Projectile projectile : world.getProjectileManager().getProjectiles()) {
            shapes.setColor(DARK);
            shapes.circle(projectile.getPosition().x, projectile.getPosition().y,
                GameConfig.PROJECTILE_RADIUS + 3f, 24);
            shapes.setColor(PROJECTILE);
            shapes.circle(projectile.getPosition().x, projectile.getPosition().y,
                GameConfig.PROJECTILE_RADIUS, 24);
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        terrainRenderer.drawOutline(
            shapes, world.getTerrain(), world.getMapDefinition().getWidth());
        shapes.end();
    }

    private void drawTank(Tank tank, Color color) {
        float x = tank.getPosition().x;
        float y = tank.getPosition().y;
        float left = x - tank.getWidth() * 0.5f;

        float trackLeft = left + 9f;
        float trackRight = left + tank.getWidth() - 9f;
        float trackY = y + 7f;
        float trackRadius = 14f;
        shapes.setColor(DARK);
        drawCapsule(trackLeft, trackRight, trackY + trackRadius, trackRadius);
        shapes.setColor(TRACK);
        drawCapsule(trackLeft + 4f, trackRight - 4f, trackY + trackRadius, trackRadius - 4f);
        shapes.setColor(WHEEL);
        shapes.circle(x - 42f, trackY + trackRadius, 8f, 18);
        shapes.circle(x, trackY + trackRadius, 8f, 18);
        shapes.circle(x + 42f, trackY + trackRadius, 8f, 18);

        float hullBottom = y + 24f;
        float hullTop = y + 57f;
        shapes.setColor(DARK);
        drawFilledQuad(
            left - 3f, hullBottom,
            left + 17f, hullTop + 4f,
            left + tank.getWidth() - 21f, hullTop + 4f,
            left + tank.getWidth() + 6f, hullBottom);
        shapes.setColor(color);
        drawFilledQuad(
            left + 4f, hullBottom + 5f,
            left + 20f, hullTop,
            left + tank.getWidth() - 24f, hullTop,
            left + tank.getWidth() - 2f, hullBottom + 5f);

        tank.getTurretPivot(pivot);
        tank.getMuzzlePosition(muzzle);
        shapes.setColor(DARK);
        shapes.rectLine(pivot.x, pivot.y, muzzle.x, muzzle.y, 18f);
        shapes.setColor(color);
        shapes.rectLine(pivot.x, pivot.y, muzzle.x, muzzle.y, 11f);
        shapes.setColor(DARK);
        shapes.circle(pivot.x, pivot.y, 25f, 28);
        shapes.setColor(color);
        shapes.circle(pivot.x, pivot.y, 20f, 28);
        shapes.setColor(METAL_HIGHLIGHT);
        shapes.rectLine(left + 22f, hullTop - 2f,
            left + tank.getWidth() - 28f, hullTop - 2f, 3f);
    }

    private void drawCapsule(float left, float right, float centerY, float radius) {
        shapes.rect(left, centerY - radius, right - left, radius * 2f);
        shapes.circle(left, centerY, radius, 24);
        shapes.circle(right, centerY, radius, 24);
    }

    private void drawFilledQuad(
        float x1, float y1,
        float x2, float y2,
        float x3, float y3,
        float x4, float y4) {
        shapes.triangle(x1, y1, x2, y2, x3, y3);
        shapes.triangle(x1, y1, x3, y3, x4, y4);
    }

    private void drawHudShapes(GameWorld world) {
        shapes.setProjectionMatrix(hudCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawHealthBar(70f, world.getPlayerTank(), PLAYER);
        drawHealthBar(GameConfig.VIEW_WIDTH - 470f, world.getBotTank(), BOT);
        drawPowerBar(70f, 850f, world.getPlayerController().getChargePercent(), PLAYER);
        drawPowerBar(GameConfig.VIEW_WIDTH - 470f, 850f,
            world.getBotController().getChargePercent(), BOT);
        shapes.setColor(new Color(0.62f, 0.16f, 0.16f, 1f));
        shapes.rect(GameConfig.EXIT_BUTTON_X, GameConfig.EXIT_BUTTON_Y,
            GameConfig.EXIT_BUTTON_WIDTH, GameConfig.EXIT_BUTTON_HEIGHT);
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.WHITE);
        shapes.rect(70f, 930f, 400f, 30f);
        shapes.rect(GameConfig.VIEW_WIDTH - 470f, 930f, 400f, 30f);
        shapes.rect(GameConfig.EXIT_BUTTON_X, GameConfig.EXIT_BUTTON_Y,
            GameConfig.EXIT_BUTTON_WIDTH, GameConfig.EXIT_BUTTON_HEIGHT);
        shapes.end();
    }

    private void drawHealthBar(float x, Tank tank, Color color) {
        float width = 400f;
        float ratio = tank.getHealth() / (float) tank.getMaxHealth();
        shapes.setColor(PANEL);
        shapes.rect(x, 930f, width, 30f);
        shapes.setColor(color);
        shapes.rect(x + 4f, 934f, (width - 8f) * ratio, 22f);
    }

    private void drawPowerBar(float x, float y, float ratio, Color color) {
        shapes.setColor(PANEL);
        shapes.rect(x, y, 400f, 20f);
        shapes.setColor(color);
        shapes.rect(x + 3f, y + 3f, 394f * MathUtils.clamp(ratio, 0f, 1f), 14f);
    }

    private void drawHudText(GameWorld world) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.draw(batch, "PLAYER", 70f, 1010f);
        drawRightAligned("BOT", GameConfig.VIEW_WIDTH - 70f, 1010f);
        font.draw(batch, world.getPlayerTank().getHealth() + "/" + world.getPlayerTank().getMaxHealth(),
            70f, 920f);
        drawRightAligned(world.getBotTank().getHealth() + "/" + world.getBotTank().getMaxHealth(),
            GameConfig.VIEW_WIDTH - 70f, 920f);
        font.draw(batch, "POWER", 70f, 840f);
        drawRightAligned("POWER", GameConfig.VIEW_WIDTH - 70f, 840f);
        drawCenteredAt("EXIT", GameConfig.EXIT_BUTTON_X + GameConfig.EXIT_BUTTON_WIDTH * 0.5f,
            GameConfig.EXIT_BUTTON_Y + 43f, 1.5f);
        font.draw(batch, "MOVE: A/D or arrows     AIM: mouse     FIRE: hold/release     ONE SHOT PER TURN",
            70f, 55f);

        if (world.getMatchState() == MatchState.COUNTDOWN) {
            drawCentered(String.valueOf(world.getCountdownNumber()), 610f, 4f);
        } else if (world.getMatchState() == MatchState.GAME_OVER) {
            drawCentered(world.getWinner() == Team.PLAYER ? "PLAYER WINS" : "BOT WINS", 635f, 3f);
            drawCentered("Press R to restart", 555f, 1.5f);
        } else {
            drawCentered(turnStatus(world), 920f, 1.7f);
        }
        batch.end();
    }

    private String turnStatus(GameWorld world) {
        if (world.getTurnPhase() == TurnPhase.PROJECTILE_IN_FLIGHT) {
            return "SHOT IN FLIGHT";
        }
        if (world.getTurnPhase() == TurnPhase.TRANSITION) {
            return "NEXT TURN";
        }
        String owner = world.getActiveTurn() == Team.PLAYER ? "YOUR TURN" : "BOT TURN";
        return owner + " - " + MathUtils.ceil(world.getTurnRemaining()) + "s";
    }

    private void drawCentered(String text, float baselineY, float scale) {
        drawCenteredAt(text, GameConfig.VIEW_WIDTH * 0.5f, baselineY, scale);
    }

    private void drawCenteredAt(String text, float centerX, float baselineY, float scale) {
        font.getData().setScale(scale);
        glyphLayout.setText(font, text);
        font.draw(batch, text, centerX - glyphLayout.width * 0.5f, baselineY);
        font.getData().setScale(2f);
    }

    private void drawRightAligned(String text, float rightX, float baselineY) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, rightX - glyphLayout.width, baselineY);
    }

    public void dispose() {
        shapes.dispose();
        batch.dispose();
        font.dispose();
        assets.dispose();
    }
}
