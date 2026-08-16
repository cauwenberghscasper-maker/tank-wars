package com.tankwars.assets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvironmentThemesTest {
    @Test
    void grasslandsThemeResolvesItsAssetFolder() {
        EnvironmentTheme theme = EnvironmentThemes.require("grasslands");
        assertEquals("grasslands", theme.getId());
        assertEquals("environment/grasslands/background.png", theme.getBackgroundPath());
    }

    @Test
    void unknownThemeFailsFastDuringScreenSetup() {
        assertThrows(IllegalArgumentException.class,
            () -> EnvironmentThemes.require("missing-theme"));
    }
}
