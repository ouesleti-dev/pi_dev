module com.example.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires okhttp;
    requires org.json;

    // Exportez le package principal (si nécessaire)
    exports com.example.javaproject;

    // Exportez explicitement le package des contrôleurs
    exports com.example.javaproject.Controllers;

    // Exportez le package des entités (si utilisé dans FXML ou réflexion)
    exports com.example.javaproject.Entities;

    // Ouvrez les packages pour permettre à JavaFX d'y accéder via réflexion
    opens com.example.javaproject.Controllers to javafx.fxml;
    opens com.example.javaproject.Entities to javafx.fxml;

    // Si vous utilisez FXML avec des contrôleurs dans d'autres packages, ajoutez-les aussi
    // opens com.example.javaproject.Views to javafx.fxml;
}