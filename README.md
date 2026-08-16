# Tank Wars

A deliberately small, code-first libGDX artillery prototype: one player tank versus one bot.

## Run the desktop game

A JDK compatible with Gradle 8.14 (Java 8 through 24) is required. Set `JAVA_HOME` to that JDK; the JDK bundled with Android Studio also works. Then, from PowerShell:

```powershell
.\gradlew.bat lwjgl3:run
```

Controls:

- Move with `A`/`D` or the left/right arrow keys.
- Aim with the mouse.
- Hold the left mouse button to charge, then release to fire.
- Press `R` after a match to restart.

The virtual playfield is fixed at 1920×1080 and scales to the desktop window. Gameplay code is in `core`; `lwjgl3` contains only the desktop launcher. Android and iOS launchers are reserved for later milestones.

## Test

```powershell
.\gradlew.bat test
```
