@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo JavaFX Application Launcher
echo ===================================================

REM Check if JavaFX SDK exists in Maven repository
set JAVAFX_PATH=%USERPROFILE%\.m2\repository\org\openjfx
if exist "%JAVAFX_PATH%" (
    echo Found JavaFX in Maven repository at: %JAVAFX_PATH%
    set JAVAFX_MODULE_PATH=%JAVAFX_PATH%\javafx-controls\17.0.2;%JAVAFX_PATH%\javafx-fxml\17.0.2;%JAVAFX_PATH%\javafx-graphics\17.0.2;%JAVAFX_PATH%\javafx-base\17.0.2
    set FOUND_JAVAFX=1
) else (
    echo JavaFX not found in Maven repository.
    set FOUND_JAVAFX=0
)

REM Check if JavaFX SDK exists in local directory
if %FOUND_JAVAFX%==0 (
    set JAVAFX_PATH=%CD%\javafx-sdk\javafx-sdk-17.0.2\lib
    if exist "%JAVAFX_PATH%" (
        echo Found JavaFX in local directory at: %JAVAFX_PATH%
        set JAVAFX_MODULE_PATH=%JAVAFX_PATH%
        set FOUND_JAVAFX=1
    ) else (
        echo JavaFX SDK not found at %JAVAFX_PATH%
        echo Running setup script first...
        call setup-javafx.bat
        if exist "%JAVAFX_PATH%" (
            echo JavaFX setup successful.
            set JAVAFX_MODULE_PATH=%JAVAFX_PATH%
            set FOUND_JAVAFX=1
        ) else (
            echo Failed to set up JavaFX SDK.
            set FOUND_JAVAFX=0
        )
    )
)

REM Check other common locations
if %FOUND_JAVAFX%==0 (
    if exist "C:\Program Files\Java\javafx-sdk-17.0.2\lib" (
        set JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-17.0.2\lib
        echo Found JavaFX at: %JAVAFX_PATH%
        set JAVAFX_MODULE_PATH=%JAVAFX_PATH%
        set FOUND_JAVAFX=1
    ) else if exist "C:\Program Files\JavaFX\javafx-sdk-17.0.2\lib" (
        set JAVAFX_PATH=C:\Program Files\JavaFX\javafx-sdk-17.0.2\lib
        echo Found JavaFX at: %JAVAFX_PATH%
        set JAVAFX_MODULE_PATH=%JAVAFX_PATH%
        set FOUND_JAVAFX=1
    ) else if exist "%USERPROFILE%\javafx-sdk-17.0.2\lib" (
        set JAVAFX_PATH=%USERPROFILE%\javafx-sdk-17.0.2\lib
        echo Found JavaFX at: %JAVAFX_PATH%
        set JAVAFX_MODULE_PATH=%JAVAFX_PATH%
        set FOUND_JAVAFX=1
    )
)

REM If JavaFX is still not found, exit
if %FOUND_JAVAFX%==0 (
    echo JavaFX SDK not found in any location.
    echo Please download JavaFX SDK 17.0.2 from: https://gluonhq.com/products/javafx/
    echo Extract it and update the JAVAFX_PATH variable in this script.
    pause
    exit /b 1
)

echo Using JavaFX from: %JAVAFX_PATH%
echo Module path: %JAVAFX_MODULE_PATH%

REM Build the project with Maven
echo Building the project with Maven...
call mvn clean package
if %ERRORLEVEL% neq 0 (
    echo Maven build failed.
    pause
    exit /b 1
)

REM Run the application with JavaFX modules
echo Running the application...
java --module-path "%JAVAFX_MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\Classe3B7-1.0-SNAPSHOT.jar Main.Main

if %ERRORLEVEL% neq 0 (
    echo Application execution failed with error code %ERRORLEVEL%.
    echo.
    echo Trying alternative method...
    java --module-path "%JAVAFX_MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Classe3B7-1.0-SNAPSHOT.jar
)

if %ERRORLEVEL% neq 0 (
    echo Both methods failed. Trying to run via Launcher class...
    java --module-path "%JAVAFX_MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
)

pause
