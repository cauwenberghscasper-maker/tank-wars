package com.tankwars.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tankwars.GameConfig;
import com.tankwars.GameWorld;
import com.tankwars.assets.GameAssets;
import com.tankwars.assets.EnvironmentTheme;
import com.tankwars.assets.EnvironmentThemes;
import com.tankwars.input.DesktopPlayerInput;
import com.tankwars.render.GameRenderer;

public final class GameScreen extends ScreenAdapter {
    private final OrthographicCamera worldCamera = new OrthographicCamera();
    private final OrthographicCamera hudCamera = new OrthographicCamera();
    private final Viewport worldViewport = new FitViewport(
        GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT, worldCamera);
    private final Viewport hudViewport = new FitViewport(
        GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT, hudCamera);
    private final DesktopPlayerInput input;
    private final GameWorld world;
    private final GameRenderer renderer;
    private final Vector2 cameraTarget = new Vector2();
    private float accumulator;

    public GameScreen() {
        worldViewport.apply(true);
        hudViewport.apply(true);
        input = new DesktopPlayerInput(worldViewport, hudViewport);
        Gdx.input.setInputProcessor(input);
        world = new GameWorld(input);
        EnvironmentTheme theme = EnvironmentThemes.require(
            world.getMapDefinition().getEnvironmentThemeId());
        GameAssets assets = new GameAssets(theme);
        assets.load();
        renderer = new GameRenderer(worldCamera, hudCamera, assets, theme);
        world.getCameraTarget(cameraTarget);
        worldCamera.position.set(clampCameraX(cameraTarget.x), GameConfig.VIEW_HEIGHT * 0.5f, 0f);
        worldCamera.update();
    }

    @Override
    public void render(float delta) {
        if (input.consumeExitRequested()) {
            Gdx.app.exit();
            return;
        }

        accumulator += Math.min(delta, GameConfig.MAX_FRAME_TIME);
        while (accumulator >= GameConfig.FIXED_TIME_STEP) {
            world.updateFixed(GameConfig.FIXED_TIME_STEP);
            accumulator -= GameConfig.FIXED_TIME_STEP;
        }
        updateCamera(Math.min(delta, GameConfig.MAX_FRAME_TIME));
        renderer.render(world);
    }

    private void updateCamera(float delta) {
        world.getCameraTarget(cameraTarget);
        float targetX = clampCameraX(cameraTarget.x);
        float targetY = clampToWorld(
            cameraTarget.y, GameConfig.VIEW_HEIGHT, world.getMapDefinition().getHeight());
        float blend = 1f - (float) Math.exp(-GameConfig.CAMERA_FOLLOW_SPEED * delta);
        worldCamera.position.x += (targetX - worldCamera.position.x) * blend;
        worldCamera.position.y += (targetY - worldCamera.position.y) * blend;
        worldCamera.update();
    }

    private float clampCameraX(float x) {
        return clampToWorld(
            x, GameConfig.VIEW_WIDTH, world.getMapDefinition().getWidth());
    }

    private float clampToWorld(float coordinate, float viewSize, float worldSize) {
        if (worldSize <= viewSize) {
            return worldSize * 0.5f;
        }
        return MathUtils.clamp(coordinate, viewSize * 0.5f, worldSize - viewSize * 0.5f);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, false);
        hudViewport.update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(input);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void pause() {
        accumulator = 0f;
        world.cancelPlayerActions();
    }

    @Override
    public void hide() {
        world.cancelPlayerActions();
    }

    @Override
    public void dispose() {
        if (Gdx.input.getInputProcessor() == input) {
            Gdx.input.setInputProcessor(null);
        }
        renderer.dispose();
    }
}
