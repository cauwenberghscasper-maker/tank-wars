package com.tankwars.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tankwars.assets.GameAssets;
import com.badlogic.gdx.graphics.OrthographicCamera;

/** Draws the distant art layer independently from gameplay terrain geometry. */
public final class EnvironmentRenderer {
    private static final int BACKGROUND_CROP_LEFT = 184;

    private final GameAssets assets;

    public EnvironmentRenderer(GameAssets assets) {
        this.assets = assets;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        Texture background = assets.getBackground();
        float viewWidth = camera.viewportWidth * camera.zoom;
        float viewHeight = camera.viewportHeight * camera.zoom;
        float left = camera.position.x - viewWidth * 0.5f;
        float bottom = camera.position.y - viewHeight * 0.5f;
        int sourceWidth = background.getWidth() - BACKGROUND_CROP_LEFT;

        batch.begin();
        batch.setColor(0.92f, 0.96f, 1f, 1f);
        batch.draw(background, left, bottom, viewWidth, viewHeight,
            BACKGROUND_CROP_LEFT, 0, sourceWidth, background.getHeight(), false, false);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }
}
