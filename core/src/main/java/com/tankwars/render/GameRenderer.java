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
import com.tankwars.control.PlayerController;
import com.tankwars.model.Projectile;
import com.tankwars.model.Tank;
import com.tankwars.model.Team;

/** Shape-only prototype renderer: no art assets or gameplay decisions. */
public final class GameRenderer {
    private static final Color SKY = new Color(0.055f, 0.085f, 0.14f, 1f);
    private static final Color GROUND = new Color(0.20f, 0.29f, 0.18f, 1f);
    private static final Color PLAYER = new Color(0.16f, 0.72f, 0.95f, 1f);
    private static final Color BOT = new Color(0.96f, 0.34f, 0.29f, 1f);
    private static final Color DARK = new Color(0.035f, 0.05f, 0.07f, 1f);
    private static final Color PANEL = new Color(0.11f, 0.15f, 0.21f, 1f);

    private final OrthographicCamera camera;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final Vector2 pivot = new Vector2();
    private final Vector2 muzzle = new Vector2();

    public GameRenderer(OrthographicCamera camera) {
        this.camera = camera;
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);
    }

    public void render(GameWorld world) {
        Gdx.gl.glClearColor(SKY.r, SKY.g, SKY.b, SKY.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapes.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        drawFilledWorld(world);
        drawOutlines(world);
        drawText(world);
    }

    private void drawFilledWorld(GameWorld world) {
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(GROUND);
        shapes.rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.GROUND_Y);

        drawTank(world.getPlayerTank(), PLAYER);
        drawTank(world.getBotTank(), BOT);

        shapes.setColor(new Color(1f, 0.86f, 0.30f, 1f));
        for (Projectile projectile : world.getProjectileManager().getProjectiles()) {
            shapes.circle(projectile.getPosition().x, projectile.getPosition().y,
                GameConfig.PROJECTILE_RADIUS, 24);
        }

        drawHealthBar(70f, world.getPlayerTank(), PLAYER);
        drawHealthBar(GameConfig.WORLD_WIDTH - 470f, world.getBotTank(), BOT);
        PlayerController playerController = world.getPlayerController();
        float playerBarPercent = playerController.isCoolingDown()
            ? playerController.getCooldownProgress()
            : playerController.getChargePercent();
        drawPowerBar(70f, 850f, playerBarPercent, PLAYER);
        drawPowerBar(GameConfig.WORLD_WIDTH - 470f, 850f,
            world.getBotController().getChargePercent(), BOT);
        shapes.setColor(new Color(0.62f, 0.16f, 0.16f, 1f));
        shapes.rect(GameConfig.EXIT_BUTTON_X, GameConfig.EXIT_BUTTON_Y,
            GameConfig.EXIT_BUTTON_WIDTH, GameConfig.EXIT_BUTTON_HEIGHT);
        shapes.end();
    }

    private void drawTank(Tank tank, Color color) {
        float x = tank.getPosition().x;
        float y = tank.getPosition().y;
        float left = x - tank.getWidth() * 0.5f;

        shapes.setColor(DARK);
        shapes.circle(x - tank.getWidth() * 0.3f, y + 16f, 21f, 24);
        shapes.circle(x + tank.getWidth() * 0.3f, y + 16f, 21f, 24);

        shapes.setColor(color);
        shapes.rect(left, y + 17f, tank.getWidth(), tank.getHeight() - 17f);
        tank.getTurretPivot(pivot);
        tank.getMuzzlePosition(muzzle);
        shapes.rectLine(pivot.x, pivot.y, muzzle.x, muzzle.y, 15f);
        shapes.circle(pivot.x, pivot.y, 23f, 24);
    }

    private void drawHealthBar(float x, Tank tank, Color color) {
        float width = 400f;
        float height = 30f;
        float ratio = tank.getHealth() / (float) tank.getMaxHealth();
        shapes.setColor(PANEL);
        shapes.rect(x, 930f, width, height);
        shapes.setColor(color);
        shapes.rect(x + 4f, 934f, (width - 8f) * ratio, height - 8f);
    }

    private void drawPowerBar(float x, float y, float ratio, Color color) {
        shapes.setColor(PANEL);
        shapes.rect(x, y, 400f, 20f);
        shapes.setColor(color);
        shapes.rect(x + 3f, y + 3f, 394f * MathUtils.clamp(ratio, 0f, 1f), 14f);
    }

    private void drawOutlines(GameWorld world) {
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(new Color(0.55f, 0.72f, 0.42f, 1f));
        shapes.line(0f, GameConfig.GROUND_Y, GameConfig.WORLD_WIDTH, GameConfig.GROUND_Y);
        shapes.setColor(Color.WHITE);
        shapes.rect(70f, 930f, 400f, 30f);
        shapes.rect(GameConfig.WORLD_WIDTH - 470f, 930f, 400f, 30f);
        shapes.rect(GameConfig.EXIT_BUTTON_X, GameConfig.EXIT_BUTTON_Y,
            GameConfig.EXIT_BUTTON_WIDTH, GameConfig.EXIT_BUTTON_HEIGHT);
        shapes.end();
    }

    private void drawText(GameWorld world) {
        batch.begin();
        font.draw(batch, "PLAYER", 70f, 1010f);
        drawRightAligned("BOT", GameConfig.WORLD_WIDTH - 70f, 1010f);
        font.draw(batch, world.getPlayerTank().getHealth() + "/" + world.getPlayerTank().getMaxHealth(),
            70f, 920f);
        drawRightAligned(world.getBotTank().getHealth() + "/" + world.getBotTank().getMaxHealth(),
            GameConfig.WORLD_WIDTH - 70f, 920f);
        PlayerController playerController = world.getPlayerController();
        String playerWeaponStatus = playerController.isCoolingDown()
            ? "RELOAD " + oneDecimalPlace(playerController.getCooldownRemaining()) + "s"
            : "POWER";
        font.draw(batch, playerWeaponStatus, 70f, 840f);
        drawRightAligned("POWER", GameConfig.WORLD_WIDTH - 70f, 840f);
        drawCenteredAt("EXIT", GameConfig.EXIT_BUTTON_X + GameConfig.EXIT_BUTTON_WIDTH * 0.5f,
            GameConfig.EXIT_BUTTON_Y + 43f, 1.5f);
        font.draw(batch, "MOVE: A/D or arrows     AIM: mouse     FIRE: hold/release left mouse     EXIT: Esc",
            70f, 55f);

        if (world.getMatchState() == MatchState.COUNTDOWN) {
            drawCentered(String.valueOf(world.getCountdownNumber()), 610f, 4f);
        } else if (world.getMatchState() == MatchState.GAME_OVER) {
            String result = world.getWinner() == Team.PLAYER ? "PLAYER WINS" : "BOT WINS";
            drawCentered(result, 635f, 3f);
            drawCentered("Press R to restart", 555f, 1.5f);
        }
        batch.end();
    }

    private void drawCentered(String text, float baselineY, float scale) {
        drawCenteredAt(text, GameConfig.WORLD_WIDTH * 0.5f, baselineY, scale);
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

    private String oneDecimalPlace(float value) {
        float roundedUp = MathUtils.ceil(value * 10f) / 10f;
        return String.valueOf(roundedUp);
    }

    public void dispose() {
        shapes.dispose();
        batch.dispose();
        font.dispose();
    }
}
