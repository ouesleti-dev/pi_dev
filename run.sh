#!/bin/bash
# Run script for JavaFX application

# Set the path to your JDK
JAVA_HOME="/usr/lib/jvm/java-17-openjdk"

# Set the path to your JavaFX SDK
PATH_TO_FX="/path/to/javafx-sdk-17.0.2/lib"

# Run the application with JavaFX modules
"$JAVA_HOME/bin/java" --module-path "$PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target/Classe3B7-1.0-SNAPSHOT.jar
