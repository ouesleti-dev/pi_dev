package Main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Launcher class to start the JavaFX application
 * This class is used to work around the JavaFX module system issues
 */
public class Launcher {
    public static void main(String[] args) {
        try {
            // Check if JavaFX modules are available
            Class.forName("javafx.application.Application");
            // If we get here, JavaFX is available, so launch the application
            Main.main(args);
        } catch (ClassNotFoundException e) {
            System.err.println("JavaFX runtime components are missing.");
            System.err.println("Attempting to locate JavaFX SDK...");

            // Try to find JavaFX SDK in common locations
            String userHome = System.getProperty("user.home");
            String javafxVersion = "17.0.2";

            // Maven repository paths (most common for JavaFX)
            String mavenRepo = userHome + "/.m2/repository/org/openjfx";
            String[] mavenModules = {
                mavenRepo + "/javafx-controls/" + javafxVersion,
                mavenRepo + "/javafx-fxml/" + javafxVersion,
                mavenRepo + "/javafx-graphics/" + javafxVersion,
                mavenRepo + "/javafx-base/" + javafxVersion
            };

            // Check if Maven modules exist
            boolean mavenModulesExist = true;
            for (String module : mavenModules) {
                if (!Files.exists(Paths.get(module))) {
                    mavenModulesExist = false;
                    break;
                }
            }

            // Standard installation paths
            String[] possiblePaths = {
                userHome + "/.m2/repository/org/openjfx",
                "C:/Program Files/Java/javafx-sdk-17.0.2/lib",
                "C:/Program Files/JavaFX/javafx-sdk-17.0.2/lib",
                "./javafx-sdk/javafx-sdk-17.0.2/lib",
                userHome + "/javafx-sdk-17.0.2/lib"
            };

            // Add current directory path
            String currentDir = System.getProperty("user.dir");
            possiblePaths = Arrays.copyOf(possiblePaths, possiblePaths.length + 1);
            possiblePaths[possiblePaths.length - 1] = currentDir + "/javafx-sdk/lib";

            String javafxPath = null;
            String modulePathCommand = null;

            // First check if Maven modules exist
            if (mavenModulesExist) {
                // Create a semicolon-separated list of module paths
                StringBuilder modulePath = new StringBuilder();
                for (int i = 0; i < mavenModules.length; i++) {
                    modulePath.append(mavenModules[i]);
                    if (i < mavenModules.length - 1) {
                        modulePath.append(";");
                    }
                }
                javafxPath = "Maven repository";
                modulePathCommand = modulePath.toString();
                System.err.println("Found JavaFX modules in Maven repository");
            } else {
                // Try standard paths
                for (String path : possiblePaths) {
                    if (Files.exists(Paths.get(path))) {
                        javafxPath = path;
                        modulePathCommand = path;
                        System.err.println("Found JavaFX at: " + javafxPath);
                        break;
                    }
                }
            }

            if (javafxPath != null) {
                System.err.println("Please run the application with the following command:");
                System.err.println("java --module-path \"" + modulePathCommand + "\" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target/classes Main.Main");
            } else {
                System.err.println("Could not locate JavaFX SDK. Please download and install JavaFX SDK 17.0.2.");
                System.err.println("Then run the application using the provided scripts or add the JavaFX modules to your module path.");
                System.err.println("Example: java --module-path \"C:/path/to/javafx-sdk/lib\" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp target/classes Main.Main");
            }

            System.exit(1);
        }
    }
}
