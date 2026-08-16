package com.tankwars.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/** Central replaceable asset catalog. Gameplay never knows file paths. */
public final class GameAssets {
    private final EnvironmentTheme theme;
    private final AssetManager manager = new AssetManager();

    public GameAssets(EnvironmentTheme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Environment theme is required");
        }
        this.theme = theme;
    }

    public void load() {
        manager.load(theme.getBackgroundPath(), Texture.class);
        manager.finishLoading();
        configure(getBackground());
    }

    private void configure(Texture texture) {
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    public Texture getBackground() {
        return manager.get(theme.getBackgroundPath(), Texture.class);
    }

    public void dispose() {
        manager.dispose();
    }
}
