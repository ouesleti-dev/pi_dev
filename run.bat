@echo off
REM Run script for JavaFX application

REM Set the path to your JDK
set JAVA_HOME=C:\Program Files\Java\jdk-17

REM Set the path to your JavaFX SDK
set PATH_TO_FX=C:\path\to\javafx-sdk-17.0.2\lib

REM Run the application with JavaFX modules
"%JAVA_HOME%\bin\java" --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Classe3B7-1.0-SNAPSHOT.jar

pause
