package Main;

// import Models.Panier; // Temporairement désactivé
// import Services.PanierService; // Temporairement désactivé
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    // Fonctionnalité panier temporairement désactivée
    // private List<Panier> paniers = new ArrayList<>();
    // private PanierService panierService = new PanierService();

    // private void initializePaniers() {
    //     // Code d'initialisation des paniers désactivé
    // }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialisation des paniers temporairement désactivée
        // initializePaniers();

        // Charger login.fxml au démarrage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Authentification/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        try {
            URL cssUrl = getClass().getResource("/styles/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("Attention : Le fichier CSS n'a pas été trouvé");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du CSS : " + e.getMessage());
        }

        primaryStage.setTitle("GoVibe - Connexion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}