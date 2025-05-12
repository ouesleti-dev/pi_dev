@echo off
REM Script pour lancer l'application JavaFX

echo Recherche de Java...

REM Vérifier si JAVA_HOME est défini
if defined JAVA_HOME (
    echo JAVA_HOME trouvé: %JAVA_HOME%
    set JAVA_CMD="%JAVA_HOME%\bin\java"
    goto :run_app
)

REM Vérifier les emplacements courants de Java
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

for %%i in (%JAVA_PATHS%) do (
    if exist %%i (
        echo Java trouvé: %%i
        set JAVA_CMD=%%i
        goto :run_app
    )
)

REM Si Java n'est pas trouvé, demander à l'utilisateur
echo Java n'a pas été trouvé automatiquement.
echo Veuillez entrer le chemin complet vers java.exe (par exemple, C:\Program Files\Java\jdk-17\bin\java.exe)
set /p JAVA_CMD=

if not exist "%JAVA_CMD%" (
    echo Le chemin vers Java n'est pas valide.
    pause
    exit /b
)

:run_app
echo Lancement de l'application avec %JAVA_CMD%...

REM Vérifier si le JAR existe
if exist "target\Classe3B7-1.0-SNAPSHOT.jar" (
    echo JAR trouvé, lancement direct...
    %JAVA_CMD% --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Classe3B7-1.0-SNAPSHOT.jar
    goto :end
)

REM Si le JAR n'existe pas, essayer de lancer la classe directement
echo JAR non trouvé, tentative de lancement via la classe Main...
%JAVA_CMD% --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Main

:end
if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors du lancement de l'application.
    echo Essayez de lancer via la classe Launcher...
    %JAVA_CMD% --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp target\classes Main.Launcher
)

pause
