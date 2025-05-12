# Guide de correction de l'erreur JavaFX

Ce guide vous aidera à résoudre l'erreur :
```
Erreur : impossible de trouver ou de charger la classe principale javafx.controls,javafx.fxml,javafx.graphics
Causé par : java.lang.ClassNotFoundException: javafx.controls,javafx.fxml,javafx.graphics
```

## Solution 1 : Configurer IntelliJ IDEA correctement

1. Ouvrez votre projet dans IntelliJ IDEA
2. Allez dans `Run > Edit Configurations`
3. Sélectionnez votre configuration de lancement ou créez une nouvelle configuration Application
4. Définissez la classe principale (Main class) à `Main.Main`
5. Dans le champ "VM options", ajoutez:
   ```
   --module-path "${user.home}/.m2/repository/org/openjfx/javafx-controls/17.0.2;${user.home}/.m2/repository/org/openjfx/javafx-fxml/17.0.2;${user.home}/.m2/repository/org/openjfx/javafx-graphics/17.0.2;${user.home}/.m2/repository/org/openjfx/javafx-base/17.0.2" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
   ```
6. Cliquez sur "Apply" et "OK"
7. Lancez votre application

## Solution 2 : Corriger le pom.xml

Le fichier pom.xml a été mis à jour pour corriger les chemins JavaFX. Les modifications incluent :

1. Correction du chemin JavaFX dans les propriétés
2. Ajout de la propriété javafx.sdk.path correcte
3. Configuration correcte du plugin JavaFX Maven

## Solution 3 : Utiliser la classe Launcher

La classe Launcher a été améliorée pour mieux détecter les modules JavaFX. Si vous rencontrez toujours des problèmes, essayez de lancer l'application via la classe Launcher :

1. Dans IntelliJ IDEA, allez dans `Run > Edit Configurations`
2. Créez une nouvelle configuration Application
3. Définissez la classe principale (Main class) à `Main.Launcher`
4. Cliquez sur "Apply" et "OK"
5. Lancez l'application

## Solution 4 : Installer JavaFX SDK manuellement

Si les solutions ci-dessus ne fonctionnent pas, vous pouvez installer JavaFX SDK manuellement :

1. Téléchargez JavaFX SDK 17.0.2 depuis : https://gluonhq.com/products/javafx/
2. Extrayez-le dans un dossier de votre choix (par exemple, `C:\javafx-sdk-17.0.2`)
3. Dans IntelliJ IDEA, allez dans `Run > Edit Configurations`
4. Dans le champ "VM options", ajoutez:
   ```
   --module-path "C:\javafx-sdk-17.0.2\lib" --add-modules=javafx.controls,javafx.fxml,javafx.graphics
   ```
   (Remplacez `C:\javafx-sdk-17.0.2\lib` par le chemin réel vers le dossier lib de votre JavaFX SDK)
5. Cliquez sur "Apply" et "OK"
6. Lancez votre application

## Vérification de l'installation de Java

Assurez-vous que Java est correctement installé sur votre système :

1. Ouvrez une invite de commande (cmd) ou PowerShell
2. Tapez `java -version`
3. Vous devriez voir la version de Java installée

Si Java n'est pas reconnu, vous devez installer Java JDK 17 et l'ajouter à votre PATH système.

## Vérification de l'installation de Maven

Si vous souhaitez utiliser Maven pour exécuter votre application :

1. Ouvrez une invite de commande (cmd) ou PowerShell
2. Tapez `mvn -version`
3. Vous devriez voir la version de Maven installée

Si Maven n'est pas reconnu, vous devez installer Maven et l'ajouter à votre PATH système.
