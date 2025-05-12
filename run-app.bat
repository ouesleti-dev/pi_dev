@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo JavaFX Application Direct Runner
echo ===================================================

REM Set the path to the Maven repository
set MAVEN_REPO=%USERPROFILE%\.m2\repository
set JAVAFX_VERSION=17.0.2

REM Set the JavaFX module path
set JAVAFX_MODULE_PATH=%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%

echo Using JavaFX from Maven repository
echo Module path: %JAVAFX_MODULE_PATH%

REM Run the application with JavaFX modules directly using Main class
echo Running the application directly...
java --module-path="%JAVAFX_MODULE_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main

pause
