package com.tankwars.input;

import com.badlogic.gdx.math.Vector2;

/** Platform-replaceable player commands. */
public interface PlayerInput {
    float getMoveAxis();

    Vector2 getAimWorldPosition();

    boolean isFirePressed();

    boolean isRestartJustPressed();
}
