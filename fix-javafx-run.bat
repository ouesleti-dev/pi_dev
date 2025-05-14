@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo JavaFX Application Fix Runner
echo ===================================================
echo.

REM Set the path to JavaFX modules based on the error message
set JAVAFX_MODULE_PATH=C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-controls\17.0.2;C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-fxml\17.0.2;C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-graphics\17.0.2;C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-base\17.0.2

REM Check if the paths exist
set MISSING_PATHS=0
for %%p in (
    "C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-controls\17.0.2"
    "C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-fxml\17.0.2"
    "C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-graphics\17.0.2"
    "C:\Users\aymen.somai\.m2\repository\org\openjfx\javafx-base\17.0.2"
) do (
    if not exist "%%~p" (
        echo Path not found: %%~p
        set /a MISSING_PATHS+=1
    )
)

if %MISSING_PATHS% GTR 0 (
    echo.
    echo Some JavaFX paths are missing. Trying alternative paths...
    
    REM Try using the current user's Maven repository
    set JAVAFX_VERSION=17.0.2
    set MAVEN_REPO=%USERPROFILE%\.m2\repository
    set JAVAFX_MODULE_PATH=%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%
    
    echo Using alternative JavaFX path: %JAVAFX_MODULE_PATH%
)

echo.
echo Using JavaFX module path: %JAVAFX_MODULE_PATH%
echo.

REM Run the application with JavaFX modules
echo Running the application...
java --module-path "%JAVAFX_MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher

if %ERRORLEVEL% neq 0 (
    echo.
    echo Application execution failed with error code %ERRORLEVEL%.
    echo.
    echo Trying to run with Main class directly...
    java --module-path "%JAVAFX_MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo Both methods failed. Trying to download JavaFX SDK...
    echo.
    
    REM Create a directory for JavaFX SDK if it doesn't exist
    if not exist "javafx-sdk" mkdir javafx-sdk
    cd javafx-sdk
    
    echo Downloading JavaFX SDK 17.0.2...
    curl -L -o javafx-sdk-17.0.2.zip https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_windows-x64_bin-sdk.zip
    
    echo Extracting JavaFX SDK...
    powershell -command "Expand-Archive -Force javafx-sdk-17.0.2.zip ."
    
    cd ..
    
    set JAVAFX_SDK_PATH=%CD%\javafx-sdk\javafx-sdk-17.0.2\lib
    echo Using downloaded JavaFX SDK: %JAVAFX_SDK_PATH%
    
    java --module-path "%JAVAFX_SDK_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
)

echo.
echo Press any key to exit...
pause > nul
