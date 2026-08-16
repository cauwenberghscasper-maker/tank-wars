package com.tankwars.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/** Keyboard/mouse implementation used by the desktop development target. */
public final class DesktopPlayerInput implements PlayerInput {
    private final Viewport viewport;
    private final Vector2 aimWorldPosition = new Vector2();

    public DesktopPlayerInput(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public float getMoveAxis() {
        float axis = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            axis -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            axis += 1f;
        }
        return axis;
    }

    @Override
    public Vector2 getAimWorldPosition() {
        aimWorldPosition.set(Gdx.input.getX(), Gdx.input.getY());
        return viewport.unproject(aimWorldPosition);
    }

    @Override
    public boolean isFirePressed() {
        return Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }

    @Override
    public boolean isRestartJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.R);
    }
}
