@echo off
echo ===================================================
echo IntelliJ IDEA JavaFX Setup Script
echo ===================================================
echo This script will help you configure IntelliJ IDEA to run JavaFX applications
echo.

REM Check if JavaFX SDK exists
set JAVAFX_PATH=%CD%\javafx-sdk\javafx-sdk-17.0.2\lib
if not exist "%JAVAFX_PATH%" (
    echo JavaFX SDK not found at %JAVAFX_PATH%
    echo Running setup script first...
    call setup-javafx.bat
    if not exist "%JAVAFX_PATH%" (
        echo Failed to set up JavaFX SDK. Please download it manually.
        echo Download from: https://gluonhq.com/products/javafx/
        echo Extract it and update the PATH_TO_FX variable in this script.
        pause
        exit /b 1
    )
)

echo.
echo ===================================================
echo IntelliJ IDEA Configuration Instructions
echo ===================================================
echo.
echo 1. Open your project in IntelliJ IDEA
echo 2. Go to Run ^> Edit Configurations
echo 3. Select your run configuration or create a new Application configuration
echo 4. Set the Main class to "Main.Main"
echo 5. In the "VM options" field, add:
echo    --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics
echo.
echo 6. Click "Apply" and "OK"
echo 7. Run your application
echo.
echo ===================================================
echo.
echo The JavaFX SDK path is: %JAVAFX_PATH%
echo.
echo Press any key to copy the VM options to clipboard...
echo.

REM Copy VM options to clipboard
echo --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics | clip

echo VM options copied to clipboard!
echo You can now paste them in IntelliJ IDEA's VM options field.
echo.
pause
