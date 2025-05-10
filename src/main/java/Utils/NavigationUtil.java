package Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe utilitaire pour gérer la navigation entre les pages
 */
public class NavigationUtil {

    /**
     * Navigue vers une nouvelle page FXML
     * @param event L'événement qui a déclenché la navigation
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    public static void navigateTo(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Appliquer les styles CSS
        String cssPath = "/styles/style.css";
        scene.getStylesheets().add(NavigationUtil.class.getResource(cssPath).toExternalForm());
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Récupère le contrôleur de la page chargée
     * @param event L'événement qui a déclenché la navigation
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param <T> Le type du contrôleur
     * @return Le contrôleur de la page
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    public static <T> T navigateToAndGetController(ActionEvent event, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        // Appliquer les styles CSS
        String cssPath = "/styles/style.css";
        scene.getStylesheets().add(NavigationUtil.class.getResource(cssPath).toExternalForm());
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        
        return loader.getController();
    }
}
