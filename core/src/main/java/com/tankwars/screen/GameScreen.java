package com.tankwars.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tankwars.GameConfig;
import com.tankwars.GameWorld;
import com.tankwars.input.DesktopPlayerInput;
import com.tankwars.render.GameRenderer;

public final class GameScreen extends ScreenAdapter {
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(
        GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
    private final DesktopPlayerInput input;
    private final GameWorld world;
    private final GameRenderer renderer;
    private float accumulator;

    public GameScreen() {
        viewport.apply(true);
        input = new DesktopPlayerInput(viewport);
        Gdx.input.setInputProcessor(input);
        world = new GameWorld(input);
        renderer = new GameRenderer(camera);
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
        renderer.render(world);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
