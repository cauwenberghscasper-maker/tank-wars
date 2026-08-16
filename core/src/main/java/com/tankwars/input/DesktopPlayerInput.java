package com.tankwars.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayDeque;
import java.util.Deque;

/** Keyboard/mouse implementation used by the desktop development target. */
public final class DesktopPlayerInput extends InputAdapter implements PlayerInput {
    private final Viewport viewport;
    private final Viewport uiViewport;
    private final Vector2 aimWorldPosition = new Vector2();
    private final Vector2 pointerWorldPosition = new Vector2();
    private final Deque<FireInputEvent> fireEvents = new ArrayDeque<FireInputEvent>();
    private boolean fireHeld;
    private boolean exitRequested;
    private int exitPointer = -1;

    public DesktopPlayerInput(Viewport viewport, Viewport uiViewport) {
        this.viewport = viewport;
        this.uiViewport = uiViewport;
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
    public FireInputEvent pollFireEvent() {
        FireInputEvent event = fireEvents.pollFirst();
        return event == null ? FireInputEvent.NONE : event;
    }

    @Override
    public boolean isFireHeld() {
        // The level check is a fallback for a release event dropped by the OS.
        if (fireHeld && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            fireHeld = false;
        }
        return fireHeld;
    }

    @Override
    public boolean isRestartJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.R);
    }

    @Override
    public boolean consumeExitRequested() {
        boolean requested = exitRequested;
        exitRequested = false;
        return requested;
    }

    @Override
    public void cancelActiveGestures() {
        fireEvents.clear();
        fireEvents.addLast(FireInputEvent.CANCEL);
        fireHeld = false;
        exitPointer = -1;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            exitRequested = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) {
            return false;
        }

        if (isExitButton(screenX, screenY)) {
            exitPointer = pointer;
            return true;
        }

        if (!fireHeld) {
            fireHeld = true;
            fireEvents.addLast(FireInputEvent.PRESS);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) {
            return false;
        }

        if (pointer == exitPointer) {
            exitPointer = -1;
            if (isExitButton(screenX, screenY)) {
                exitRequested = true;
            }
            return true;
        }

        if (fireHeld) {
            fireHeld = false;
            fireEvents.addLast(FireInputEvent.RELEASE);
        }
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (pointer == exitPointer) {
            exitPointer = -1;
            return true;
        }
        if (button == Input.Buttons.LEFT && fireHeld) {
            fireHeld = false;
            fireEvents.addLast(FireInputEvent.CANCEL);
            return true;
        }
        return false;
    }

    private boolean isExitButton(int screenX, int screenY) {
        pointerWorldPosition.set(screenX, screenY);
        uiViewport.unproject(pointerWorldPosition);
        return UiHitAreas.isExitButton(pointerWorldPosition.x, pointerWorldPosition.y);
    }
}
