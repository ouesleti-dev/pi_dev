# JavaFX Application Setup Guide

## Fixing "Module javafx.controls not found" Error

This guide will help you fix the "Module javafx.controls not found" error that occurs when trying to run a JavaFX application.

## Quick Solution

1. Run the `setup-javafx.bat` script to automatically download and set up JavaFX SDK:
   ```
   setup-javafx.bat
   ```

2. After the setup is complete, run your application using the provided script:
   ```
   run-javafx-app.bat
   ```

## Manual Setup

If the automatic setup doesn't work, follow these manual steps:

### Step 1: Download JavaFX SDK

1. Download JavaFX SDK 17.0.2 from: https://gluonhq.com/products/javafx/
   - Select "JavaFX SDK" as the product
   - Select "17.0.2" as the version
   - Select your operating system (Windows, macOS, or Linux)
   - Click "Download"

2. Extract the downloaded ZIP file to a location on your computer
   - Remember the path where you extracted it (e.g., `C:\javafx-sdk-17.0.2`)

### Step 2: Run Your Application with JavaFX Modules

#### Option 1: Using Command Line

Run your application with the following command:

```
java --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Classe3B7-1.0-SNAPSHOT.jar
```

Replace `C:\path\to\javafx-sdk-17.0.2\lib` with the actual path to your JavaFX SDK lib folder.

#### Option 2: Using IntelliJ IDEA

1. Open your project in IntelliJ IDEA
2. Go to Run > Edit Configurations
3. Select your run configuration or create a new Application configuration
4. Set the Main class to `Main.Main`
5. In the "VM options" field, add:
   ```
   --module-path "C:\path\to\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics
   ```
   (Replace `C:\path\to\javafx-sdk-17.0.2\lib` with the actual path to your JavaFX SDK lib folder)
6. Click "Apply" and "OK"
7. Run your application

#### Option 3: Using Maven

1. Edit the `pom.xml` file to update the JavaFX SDK path:
   ```xml
   <properties>
       <!-- other properties -->
       <javafx.sdk.path>C:\path\to\javafx-sdk-17.0.2\lib</javafx.sdk.path>
   </properties>
   ```
   Replace `C:\path\to\javafx-sdk-17.0.2\lib` with the actual path to your JavaFX SDK lib folder.

2. Run your application using Maven:
   ```
   mvn clean javafx:run
   ```

## Troubleshooting

If you still encounter issues:

1. Make sure your Java version matches the JavaFX version (both should be 17.x)
   - Check your Java version with: `java -version`
   - If needed, download JDK 17 from: https://www.oracle.com/java/technologies/downloads/#java17

2. Verify that the path to the JavaFX SDK is correct
   - The path should point to the `lib` folder inside the JavaFX SDK directory
   - Make sure there are no spaces in the path or use quotes around it

3. Check if the JavaFX modules are in the correct location
   - The `lib` folder should contain files like `javafx.controls.jar`, `javafx.fxml.jar`, etc.

4. Try rebuilding the project with `mvn clean install`

5. If using IntelliJ IDEA, try invalidating caches and restarting:
   - Go to File > Invalidate Caches / Restart
   - Select "Invalidate and Restart"
