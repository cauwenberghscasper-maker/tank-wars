package com.tankwars.assets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Central theme catalog. Add a definition here, then reference its ID from a map. */
public final class EnvironmentThemes {
    private static final Map<String, EnvironmentTheme> THEMES;

    static {
        Map<String, EnvironmentTheme> themes = new HashMap<String, EnvironmentTheme>();
        register(themes, new EnvironmentTheme(
            "grasslands",
            "environment/grasslands/background.png",
            0x171f29ff,
            0x22343aff,
            0x314a49ff,
            0x61765fff,
            0xa0aa7eff,
            0x18292eff));
        THEMES = Collections.unmodifiableMap(themes);
    }

    private EnvironmentThemes() {
    }

    private static void register(
        Map<String, EnvironmentTheme> themes, EnvironmentTheme theme) {
        if (themes.put(theme.getId(), theme) != null) {
            throw new IllegalStateException("Duplicate environment theme: " + theme.getId());
        }
    }

    public static EnvironmentTheme require(String id) {
        EnvironmentTheme theme = THEMES.get(id);
        if (theme == null) {
            throw new IllegalArgumentException("Unknown environment theme: " + id);
        }
        return theme;
    }
}
