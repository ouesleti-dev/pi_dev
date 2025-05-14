@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo JavaFX Setup and Fix Script
echo ===================================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Maven is not installed or not in the PATH.
    echo Please install Maven and try again.
    pause
    exit /b 1
)

REM Get the current user's home directory
set USER_HOME=%USERPROFILE%

REM Set JavaFX version
set JAVAFX_VERSION=17.0.2

REM Check if JavaFX dependencies exist in Maven repository
set MAVEN_REPO=%USER_HOME%\.m2\repository\org\openjfx
set JAVAFX_CONTROLS=%MAVEN_REPO%\javafx-controls\%JAVAFX_VERSION%
set JAVAFX_FXML=%MAVEN_REPO%\javafx-fxml\%JAVAFX_VERSION%
set JAVAFX_GRAPHICS=%MAVEN_REPO%\javafx-graphics\%JAVAFX_VERSION%
set JAVAFX_BASE=%MAVEN_REPO%\javafx-base\%JAVAFX_VERSION%

set MISSING_DEPS=0
if not exist "%JAVAFX_CONTROLS%" set /a MISSING_DEPS+=1
if not exist "%JAVAFX_FXML%" set /a MISSING_DEPS+=1
if not exist "%JAVAFX_GRAPHICS%" set /a MISSING_DEPS+=1
if not exist "%JAVAFX_BASE%" set /a MISSING_DEPS+=1

if %MISSING_DEPS% GTR 0 (
    echo Some JavaFX dependencies are missing in your Maven repository.
    echo Running Maven to download dependencies...
    
    REM Run Maven to download dependencies
    call mvn dependency:resolve
    
    if %ERRORLEVEL% neq 0 (
        echo Failed to download dependencies with Maven.
        echo Trying to download JavaFX SDK directly...
        goto download_javafx
    )
    
    echo Maven dependencies downloaded successfully.
) else (
    echo All JavaFX dependencies found in Maven repository.
)

REM Create a run script with the correct paths
echo Creating run script with correct paths...
(
echo @echo off
echo setlocal enabledelayedexpansion
echo.
echo REM Set the path to JavaFX modules
echo set JAVAFX_MODULE_PATH=%MAVEN_REPO%\javafx-controls\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-fxml\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-graphics\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-base\%JAVAFX_VERSION%
echo.
echo echo Running the application with JavaFX modules...
echo java --module-path "%%JAVAFX_MODULE_PATH%%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
echo.
echo if %%ERRORLEVEL%% neq 0 ^(
echo     echo Failed to run with Launcher. Trying Main class directly...
echo     java --module-path "%%JAVAFX_MODULE_PATH%%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main
echo ^)
echo.
echo pause
) > run-with-maven-repo.bat

echo Created run-with-maven-repo.bat with correct Maven repository paths.

REM Check if we need to download JavaFX SDK
:check_javafx_sdk
if not exist "javafx-sdk\javafx-sdk-%JAVAFX_VERSION%\lib" (
    echo JavaFX SDK not found locally.
    
    :download_javafx
    echo Downloading JavaFX SDK %JAVAFX_VERSION%...
    
    REM Create directory for JavaFX SDK
    if not exist "javafx-sdk" mkdir javafx-sdk
    cd javafx-sdk
    
    REM Download JavaFX SDK
    curl -L -o javafx-sdk-%JAVAFX_VERSION%.zip https://download2.gluonhq.com/openjfx/%JAVAFX_VERSION%/openjfx-%JAVAFX_VERSION%_windows-x64_bin-sdk.zip
    
    if %ERRORLEVEL% neq 0 (
        echo Failed to download JavaFX SDK.
        cd ..
        goto create_intellij_config
    )
    
    echo Extracting JavaFX SDK...
    powershell -command "Expand-Archive -Force javafx-sdk-%JAVAFX_VERSION%.zip ."
    
    cd ..
    
    echo JavaFX SDK downloaded and extracted successfully.
    
    REM Create a run script with the downloaded JavaFX SDK
    echo Creating run script with downloaded JavaFX SDK...
    (
    echo @echo off
    echo setlocal enabledelayedexpansion
    echo.
    echo REM Set the path to JavaFX SDK
    echo set JAVAFX_SDK_PATH=%%CD%%\javafx-sdk\javafx-sdk-%JAVAFX_VERSION%\lib
    echo.
    echo echo Running the application with downloaded JavaFX SDK...
    echo java --module-path "%%JAVAFX_SDK_PATH%%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
    echo.
    echo if %%ERRORLEVEL%% neq 0 ^(
    echo     echo Failed to run with Launcher. Trying Main class directly...
    echo     java --module-path "%%JAVAFX_SDK_PATH%%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main
    echo ^)
    echo.
    echo pause
    ) > run-with-sdk.bat
    
    echo Created run-with-sdk.bat with downloaded JavaFX SDK.
) else (
    echo JavaFX SDK found locally.
    
    REM Create a run script with the local JavaFX SDK
    echo Creating run script with local JavaFX SDK...
    (
    echo @echo off
    echo setlocal enabledelayedexpansion
    echo.
    echo REM Set the path to JavaFX SDK
    echo set JAVAFX_SDK_PATH=%%CD%%\javafx-sdk\javafx-sdk-%JAVAFX_VERSION%\lib
    echo.
    echo echo Running the application with local JavaFX SDK...
    echo java --module-path "%%JAVAFX_SDK_PATH%%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
    echo.
    echo if %%ERRORLEVEL%% neq 0 ^(
    echo     echo Failed to run with Launcher. Trying Main class directly...
    echo     java --module-path "%%JAVAFX_SDK_PATH%%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main
    echo ^)
    echo.
    echo pause
    ) > run-with-sdk.bat
    
    echo Created run-with-sdk.bat with local JavaFX SDK.
)

:create_intellij_config
echo Creating IntelliJ IDEA run configuration instructions...
(
echo # IntelliJ IDEA Run Configuration
echo 
echo To configure your project in IntelliJ IDEA:
echo 
echo 1. Open your project in IntelliJ IDEA
echo 2. Go to Run ^> Edit Configurations
echo 3. Click the + button and select "Application"
echo 4. Set the following:
echo    - Name: GoVibe Application
echo    - Main class: Main.Main
echo    - VM options: --module-path "PATH_TO_JAVAFX" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
echo      (Replace PATH_TO_JAVAFX with one of the following:)
echo      - Maven repository: %MAVEN_REPO%\javafx-controls\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-fxml\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-graphics\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-base\%JAVAFX_VERSION%
echo      - Local JavaFX SDK: %%PROJECT_DIR%%\javafx-sdk\javafx-sdk-%JAVAFX_VERSION%\lib
echo    - Working directory: %%PROJECT_DIR%%
echo    - Use classpath of module: Classe3B7
echo 5. Click OK to save the configuration
echo 
echo Now you can run your application from IntelliJ IDEA by selecting the "GoVibe Application" configuration and clicking the Run button.
) > intellij-idea-config.md

echo Created intellij-idea-config.md with IntelliJ IDEA configuration instructions.

echo.
echo ===================================================
echo Setup Complete!
echo ===================================================
echo.
echo You can now run your application using one of the following scripts:
echo.
echo 1. run-with-maven-repo.bat - Uses JavaFX from Maven repository
echo 2. run-with-sdk.bat - Uses downloaded/local JavaFX SDK
echo.
echo For IntelliJ IDEA configuration, see intellij-idea-config.md
echo.
echo Press any key to exit...
pause > nul
