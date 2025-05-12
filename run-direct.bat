@echo off
echo ===================================================
echo Direct JavaFX Application Runner
echo ===================================================
echo.

REM Set the path to JavaFX modules in your Maven repository
set JAVAFX_PATH=%USERPROFILE%\.m2\repository\org\openjfx
set JAVAFX_VERSION=17.0.2

REM Run the application with the exact module path from your error message
echo Running the application with exact module path...
java --module-path %JAVAFX_PATH%\javafx-controls\%JAVAFX_VERSION%;%JAVAFX_PATH%\javafx-fxml\%JAVAFX_VERSION%;%JAVAFX_PATH%\javafx-graphics\%JAVAFX_VERSION%;%JAVAFX_PATH%\javafx-base\%JAVAFX_VERSION% --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main

if %ERRORLEVEL% neq 0 (
    echo Direct execution failed. Trying to run via Launcher...
    java --module-path %JAVAFX_PATH%\javafx-controls\%JAVAFX_VERSION%;%JAVAFX_PATH%\javafx-fxml\%JAVAFX_VERSION%;%JAVAFX_PATH%\javafx-graphics\%JAVAFX_VERSION%;%JAVAFX_PATH%\javafx-base\%JAVAFX_VERSION% --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
)

pause
