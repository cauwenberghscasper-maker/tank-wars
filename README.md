# Tank Wars

A deliberately small, code-first libGDX artillery prototype: one player tank versus one bot.

## Quick start

A JDK compatible with Gradle 8.14 (Java 8 through 24) and Git are required. Set `JAVA_HOME` to the JDK; the JDK bundled with Android Studio also works.

For a fresh checkout, run these commands in PowerShell:

```powershell
git clone https://github.com/cauwenberghscasper-maker/tank-wars.git
cd tank-wars
.\run-game.bat
```

The checked-in Gradle wrapper downloads Gradle and the game dependencies on the first run. No system-wide Gradle installation is needed.

For an existing checkout, pull only fast-forward changes and start the game with one command:

```powershell
.\run-game.bat --pull
```

You can also run Gradle directly:

```powershell
.\gradlew.bat lwjgl3:run
```

Controls:

- Move with `A`/`D` or the left/right arrow keys.
- Aim with the mouse.
- Hold the left mouse button to charge, then release to fire.
- Press `R` after a match to restart.

The virtual playfield is fixed at 1920×1080 and scales to the desktop window. Gameplay code is in `core`; `lwjgl3` contains only the desktop launcher. Android and iOS launchers are reserved for later milestones.

## Test the project

```powershell
.\gradlew.bat test
```
