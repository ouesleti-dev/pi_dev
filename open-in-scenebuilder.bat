@echo off
REM Script pour ouvrir les fichiers FXML dans SceneBuilder

REM Demander le chemin vers SceneBuilder
echo Veuillez entrer le chemin complet vers SceneBuilder.exe (par exemple, C:\Program Files\SceneBuilder\SceneBuilder.exe)
set /p SCENEBUILDER_PATH=

REM Vérifier si le chemin existe
if not exist "%SCENEBUILDER_PATH%" (
    echo Le chemin vers SceneBuilder n'est pas valide.
    pause
    exit /b
)

REM Ouvrir chaque fichier FXML dans SceneBuilder
echo Ouverture des fichiers FXML dans SceneBuilder...

start "" "%SCENEBUILDER_PATH%" "%~dp0src\main\resources\Authentification\login.fxml"
start "" "%SCENEBUILDER_PATH%" "%~dp0src\main\resources\Authentification\register.fxml"
start "" "%SCENEBUILDER_PATH%" "%~dp0src\main\resources\Authentification\LivraisonForm.fxml"
start "" "%SCENEBUILDER_PATH%" "%~dp0src\main\resources\Authentification\PaiementEnLigneForm.fxml"
start "" "%SCENEBUILDER_PATH%" "%~dp0src\main\resources\Authentification\Panier.fxml"

echo Tous les fichiers FXML ont été ouverts dans SceneBuilder.
pause
