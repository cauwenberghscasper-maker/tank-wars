@echo off
setlocal
cd /d "%~dp0"

if /I "%~1"=="--pull" (
    git pull --ff-only
    if errorlevel 1 (
        echo Update failed. Resolve any Git issue shown above, then try again.
        exit /b 1
    )
)

call gradlew.bat lwjgl3:run
set EXIT_CODE=%ERRORLEVEL%
endlocal & exit /b %EXIT_CODE%
