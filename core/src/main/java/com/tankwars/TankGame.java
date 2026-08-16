package com.tankwars;

import com.badlogic.gdx.Game;
import com.tankwars.screen.GameScreen;

/** Platform-independent game entry point. */
public final class TankGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
