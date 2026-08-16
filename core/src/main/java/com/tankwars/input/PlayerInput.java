package com.tankwars.input;

import com.badlogic.gdx.math.Vector2;

/** Platform-replaceable player commands. */
public interface PlayerInput {
    float getMoveAxis();

    Vector2 getAimWorldPosition();

    FireInputEvent pollFireEvent();

    boolean isFireHeld();

    boolean isRestartJustPressed();

    boolean consumeExitRequested();

    void cancelActiveGestures();
}
