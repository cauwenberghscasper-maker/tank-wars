package com.tankwars.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tankwars.TankGame;

/** Desktop-only launcher. All gameplay lives in the core module. */
public final class Lwjgl3Launcher {
    private Lwjgl3Launcher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Tank Wars v0.1");
        configuration.setWindowedMode(1280, 720);
        configuration.setWindowSizeLimits(960, 540, -1, -1);
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);
        new Lwjgl3Application(new TankGame(), configuration);
    }
}

