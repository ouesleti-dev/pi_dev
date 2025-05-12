# JavaFX Application Setup Guide

## Fixing "des composants d'exécution JavaFX obligatoires pour exécuter cette application sont manquants" Error

This error occurs when the JavaFX runtime components required to run the application are missing. Follow these steps to fix it:

## Option 1: Run with Maven

1. Make sure you have Maven installed
2. Open a terminal/command prompt in the project directory
3. Run the application using Maven:
   ```
   mvn clean javafx:run
   ```

## Option 2: Run with IntelliJ IDEA

1. Open your project in IntelliJ IDEA
2. Go to Run > Edit Configurations
3. Select your run configuration or create a new Application configuration
4. In the "VM options" field, add:
   ```
   --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics
   ```
   (Replace "C:\path\to\javafx-sdk-17.0.2\lib" with the actual path to your JavaFX SDK lib folder)
5. Click "Apply" and "OK"
6. Run your application

## Option 3: Use the Run Scripts

1. Edit the `run.bat` (Windows) or `run.sh` (Linux/Mac) file
2. Update the `JAVA_HOME` and `PATH_TO_FX` variables to match your system
3. Run the script

## Downloading JavaFX SDK

If you don't have the JavaFX SDK:

1. Download JavaFX SDK 17.0.2 from: https://gluonhq.com/products/javafx/
2. Extract the downloaded file to a location on your computer
3. Use the path to the "lib" folder in the extracted directory for the `--module-path` option

## Troubleshooting

If you still encounter issues:

1. Make sure your Java version matches the JavaFX version (both should be 17.x)
2. Verify that the path to the JavaFX SDK is correct
3. Ensure all JavaFX dependencies are correctly specified in your pom.xml
4. Try rebuilding the project with `mvn clean install`
