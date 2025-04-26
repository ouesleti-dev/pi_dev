package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Authentification/register.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Optionnel : Ajouter un fichier CSS
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
