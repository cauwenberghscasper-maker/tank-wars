package com.tankwars.input;

import com.tankwars.GameConfig;

/** Pure world-space hit tests shared by input implementations. */
public final class UiHitAreas {
    private UiHitAreas() {
    }

    public static boolean isExitButton(float worldX, float worldY) {
        return worldX >= GameConfig.EXIT_BUTTON_X
            && worldX <= GameConfig.EXIT_BUTTON_X + GameConfig.EXIT_BUTTON_WIDTH
            && worldY >= GameConfig.EXIT_BUTTON_Y
            && worldY <= GameConfig.EXIT_BUTTON_Y + GameConfig.EXIT_BUTTON_HEIGHT;
    }
}
