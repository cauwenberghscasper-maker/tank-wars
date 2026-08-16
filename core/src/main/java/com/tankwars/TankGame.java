package com.tankwars;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/** Desktop-independent game entry point. */
public final class TankGame extends ApplicationAdapter {
    @Override
    public void render() {
        Gdx.gl.glClearColor(0.08f, 0.11f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}

