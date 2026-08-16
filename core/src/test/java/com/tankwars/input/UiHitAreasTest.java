package com.tankwars.input;

import com.tankwars.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiHitAreasTest {
    @Test
    void exitButtonAcceptsItsCenterAndEdges() {
        assertTrue(UiHitAreas.isExitButton(
            GameConfig.EXIT_BUTTON_X + GameConfig.EXIT_BUTTON_WIDTH * 0.5f,
            GameConfig.EXIT_BUTTON_Y + GameConfig.EXIT_BUTTON_HEIGHT * 0.5f));
        assertTrue(UiHitAreas.isExitButton(
            GameConfig.EXIT_BUTTON_X, GameConfig.EXIT_BUTTON_Y));
    }

    @Test
    void exitButtonRejectsNearbyGameplayArea() {
        assertFalse(UiHitAreas.isExitButton(
            GameConfig.EXIT_BUTTON_X - 1f,
            GameConfig.EXIT_BUTTON_Y + GameConfig.EXIT_BUTTON_HEIGHT * 0.5f));
        assertFalse(UiHitAreas.isExitButton(
            GameConfig.EXIT_BUTTON_X + GameConfig.EXIT_BUTTON_WIDTH * 0.5f,
            GameConfig.EXIT_BUTTON_Y - 1f));
    }
}
