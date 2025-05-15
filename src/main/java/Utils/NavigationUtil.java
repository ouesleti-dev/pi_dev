package Utils;

import Controllers.ResetPasswordController;
import Controllers.VerifyCodeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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

        // Appliquer les styles CSS de base
        String cssPath = "/styles/style.css";
        URL cssUrl = NavigationUtil.class.getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Appliquer le CSS spécifique pour le panier si nécessaire
        if (fxmlPath.contains("Panier.fxml")) {
            String panierCssPath = "/styles/panier-style.css";
            URL panierCssUrl = NavigationUtil.class.getResource(panierCssPath);
            if (panierCssUrl != null) {
                scene.getStylesheets().add(panierCssUrl.toExternalForm());
            }
        }

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

        // Appliquer les styles CSS de base
        String cssPath = "/styles/style.css";
        URL cssUrl = NavigationUtil.class.getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Appliquer le CSS spécifique pour le panier si nécessaire
        if (fxmlPath.contains("Panier.fxml")) {
            String panierCssPath = "/styles/panier-style.css";
            URL panierCssUrl = NavigationUtil.class.getResource(panierCssPath);
            if (panierCssUrl != null) {
                scene.getStylesheets().add(panierCssUrl.toExternalForm());
            }
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

        return loader.getController();
    }

    /**
     * Navigue vers une nouvelle page FXML avec un paramètre userId
     * @param event L'événement qui a déclenché la navigation
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     * @param userId L'ID de l'utilisateur à passer au contrôleur
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    public static void navigateTo(ActionEvent event, String fxmlPath, String title, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Appliquer les styles CSS de base
        String cssPath = "/styles/style.css";
        URL cssUrl = NavigationUtil.class.getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Initialiser le contrôleur avec l'ID de l'utilisateur
        if (fxmlPath.contains("verify_code.fxml")) {
            VerifyCodeController controller = loader.getController();
            controller.initData(userId);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Navigue vers une nouvelle page FXML avec un paramètre userId et resetCodeId
     * @param event L'événement qui a déclenché la navigation
     * @param fxmlPath Le chemin vers le fichier FXML
     * @param title Le titre de la fenêtre
     * @param userId L'ID de l'utilisateur à passer au contrôleur
     * @param resetCodeId L'ID du code de réinitialisation à passer au contrôleur
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    public static void navigateTo(ActionEvent event, String fxmlPath, String title, int userId, int resetCodeId) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Appliquer les styles CSS de base
        String cssPath = "/styles/style.css";
        URL cssUrl = NavigationUtil.class.getResource(cssPath);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Initialiser le contrôleur avec l'ID de l'utilisateur et l'ID du code de réinitialisation
        if (fxmlPath.contains("reset_password.fxml")) {
            ResetPasswordController controller = loader.getController();
            controller.initData(userId, resetCodeId);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
