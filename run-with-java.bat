@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo JavaFX Application Runner with Java Detection
echo ===================================================

REM Check for Java in common locations
set JAVA_PATHS=^
"C:\Program Files\Java\jdk-17\bin\java.exe" ^
"C:\Program Files\Java\jdk-17.0.2\bin\java.exe" ^
"C:\Program Files\Java\jdk-17.0.1\bin\java.exe" ^
"C:\Program Files\Java\jdk-17.0.0\bin\java.exe" ^
"C:\Program Files\Java\jdk-11\bin\java.exe" ^
"C:\Program Files\Java\jdk1.8.0_301\bin\java.exe" ^
"C:\Program Files\Java\jre1.8.0_301\bin\java.exe" ^
"C:\Program Files\Eclipse Adoptium\jdk-17.0.2.8-hotspot\bin\java.exe" ^
"C:\Program Files\Eclipse Adoptium\jdk-11.0.14.9-hotspot\bin\java.exe" ^
"C:\Program Files\Eclipse Foundation\jdk-17.0.2.8-hotspot\bin\java.exe" ^
"C:\Program Files\Eclipse Foundation\jdk-11.0.14.9-hotspot\bin\java.exe" ^
"C:\Program Files\Amazon Corretto\jdk17.0.2_8\bin\java.exe" ^
"C:\Program Files\Amazon Corretto\jdk11.0.14_9\bin\java.exe"

set JAVA_CMD=

for %%i in (%JAVA_PATHS%) do (
    if exist %%i (
        echo Java found: %%i
        set JAVA_CMD=%%i
        goto :found_java
    )
)

echo Java not found in common locations.
echo Checking if 'java' is in PATH...

where java >nul 2>nul
if %ERRORLEVEL% equ 0 (
    echo Java found in PATH
    set JAVA_CMD=java
    goto :found_java
)

echo Java not found. Please install Java 17 or set JAVA_HOME.
pause
exit /b 1

:found_java
echo Using Java: %JAVA_CMD%

REM Set the path to the Maven repository
set MAVEN_REPO=%USERPROFILE%\.m2\repository
set JAVAFX_VERSION=17.0.2

REM Set the JavaFX module path
set JAVAFX_MODULE_PATH=%MAVEN_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%;%MAVEN_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%

echo Using JavaFX from Maven repository
echo Module path: %JAVAFX_MODULE_PATH%

REM Run the application with JavaFX modules
echo Running the application...
%JAVA_CMD% --module-path="%JAVAFX_MODULE_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main

if %ERRORLEVEL% neq 0 (
    echo Application execution failed with error code %ERRORLEVEL%.
    echo.
    echo Trying to run via Launcher class...
    %JAVA_CMD% --module-path="%JAVAFX_MODULE_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
)

pause
