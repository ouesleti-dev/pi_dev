@echo off
echo ===================================================
echo JavaFX Setup Script
echo ===================================================
echo This script will download and set up JavaFX SDK 17.0.2
echo.

REM Create a directory for JavaFX SDK
if not exist "javafx-sdk" mkdir javafx-sdk
cd javafx-sdk

echo Downloading JavaFX SDK 17.0.2...
curl -L -o javafx-sdk-17.0.2.zip https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_windows-x64_bin-sdk.zip

echo Extracting JavaFX SDK...
powershell -command "Expand-Archive -Force javafx-sdk-17.0.2.zip ."

echo JavaFX SDK has been set up successfully!
echo.
echo The JavaFX SDK is located at: %CD%\javafx-sdk-17.0.2\lib
echo.
echo Please use this path in your run configuration.
echo.

cd ..

REM Create a run.bat file with the correct path
echo Creating run.bat file...
(
echo @echo off
echo REM Run script for JavaFX application
echo.
echo REM Set the path to JavaFX SDK
echo set PATH_TO_FX=%CD%\javafx-sdk\javafx-sdk-17.0.2\lib
echo.
echo REM Run the application with JavaFX modules
echo java --module-path "%%PATH_TO_FX%%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\Classe3B7-1.0-SNAPSHOT.jar Main.Main
echo.
echo pause
) > run-app.bat

echo Created run-app.bat file.
echo.
echo Setup complete! You can now run your application using run-app.bat
echo.
pause
