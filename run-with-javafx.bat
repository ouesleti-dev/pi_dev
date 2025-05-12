@echo off
REM Run script for JavaFX application with explicit module path

REM Build the project first
call mvn clean package

REM Run the application with JavaFX modules
java --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Classe3B7-1.0-SNAPSHOT.jar

pause
