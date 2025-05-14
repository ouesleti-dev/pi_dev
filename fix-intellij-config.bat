@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo IntelliJ IDEA JavaFX Configuration Helper
echo ===================================================
echo.

REM Get the current user's home directory
set USER_HOME=%USERPROFILE%

REM Set JavaFX version
set JAVAFX_VERSION=17.0.2

REM Set the path to JavaFX modules in Maven repository
set MAVEN_REPO=%USER_HOME%\.m2\repository\org\openjfx
set JAVAFX_MODULE_PATH=%MAVEN_REPO%\javafx-controls\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-fxml\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-graphics\%JAVAFX_VERSION%;%MAVEN_REPO%\javafx-base\%JAVAFX_VERSION%

REM Check if JavaFX SDK exists locally
set LOCAL_SDK_PATH=%CD%\javafx-sdk\javafx-sdk-%JAVAFX_VERSION%\lib
set SDK_EXISTS=0
if exist "%LOCAL_SDK_PATH%" set SDK_EXISTS=1

echo IntelliJ IDEA Run Configuration
echo.
echo To configure your project in IntelliJ IDEA:
echo.
echo 1. Open your project in IntelliJ IDEA
echo 2. Go to Run ^> Edit Configurations
echo 3. Click the + button and select "Application"
echo 4. Set the following:
echo    - Name: GoVibe Application
echo    - Main class: Main.Main
echo    - VM options: 

if %SDK_EXISTS%==1 (
    echo      --module-path "%LOCAL_SDK_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
    echo      (Using local JavaFX SDK)
) else (
    echo      --module-path "%JAVAFX_MODULE_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
    echo      (Using Maven repository JavaFX)
)

echo    - Working directory: %CD%
echo    - Use classpath of module: Classe3B7
echo 5. Click OK to save the configuration
echo.
echo Now you can run your application from IntelliJ IDEA by selecting the "GoVibe Application" configuration and clicking the Run button.
echo.

REM Create a .idea directory if it doesn't exist
if not exist ".idea" mkdir .idea
if not exist ".idea\runConfigurations" mkdir ".idea\runConfigurations"

REM Create an IntelliJ IDEA run configuration file
echo Creating IntelliJ IDEA run configuration file...

if %SDK_EXISTS%==1 (
    set VM_OPTIONS=--module-path "%LOCAL_SDK_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
) else (
    set VM_OPTIONS=--module-path "%JAVAFX_MODULE_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
)

(
echo ^<component name="ProjectRunConfigurationManager"^>
echo   ^<configuration default="false" name="GoVibe Application" type="Application" factoryName="Application"^>
echo     ^<option name="MAIN_CLASS_NAME" value="Main.Main" /^>
echo     ^<module name="Classe3B7" /^>
echo     ^<option name="VM_PARAMETERS" value="%VM_OPTIONS%" /^>
echo     ^<option name="WORKING_DIRECTORY" value="$PROJECT_DIR$" /^>
echo     ^<method v="2"^>
echo       ^<option name="Make" enabled="true" /^>
echo     ^</method^>
echo   ^</configuration^>
echo ^</component^>
) > .idea\runConfigurations\GoVibe_Application.xml

echo Created IntelliJ IDEA run configuration file: .idea\runConfigurations\GoVibe_Application.xml
echo.
echo The next time you open your project in IntelliJ IDEA, the "GoVibe Application" run configuration will be available.
echo.
echo Press any key to exit...
pause > nul
