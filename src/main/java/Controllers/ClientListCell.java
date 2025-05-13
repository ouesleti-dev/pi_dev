package Controllers;

import Models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ClientListCell extends ListCell<User> {
    
    private HBox content;
    private VBox detailsBox;
    private Label nameLabel;
    private Label emailLabel;
    private Label phoneLabel;
    private Label statusLabel;
    
    public ClientListCell() {
        super();
        
        // Créer les composants
        nameLabel = new Label();
        nameLabel.getStyleClass().add("item-title");
        
        emailLabel = new Label();
        emailLabel.getStyleClass().add("item-details");
        
        phoneLabel = new Label();
        phoneLabel.getStyleClass().add("item-details");
        
        statusLabel = new Label();
        statusLabel.getStyleClass().add("item-status");
        
        // Organiser les détails dans une VBox
        detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(nameLabel, emailLabel, phoneLabel);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);
        
        // Créer le conteneur principal
        content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(detailsBox, statusLabel);
    }
    
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        
        if (empty || user == null) {
            setGraphic(null);
        } else {
            // Définir les valeurs
            nameLabel.setText(user.getPrenom() + " " + user.getNom());
            emailLabel.setText("Email: " + user.getEmail());
            phoneLabel.setText("Téléphone: " + (user.getTelephone() != null ? user.getTelephone() : "Non renseigné"));
            
            // Définir le statut avec style
            statusLabel.setText(user.isVerified() ? "Vérifié" : "Non vérifié");
            statusLabel.getStyleClass().removeAll("status-valide", "status-abondonne");
            
            if (user.isVerified()) {
                statusLabel.getStyleClass().add("status-valide");
            } else {
                statusLabel.getStyleClass().add("status-abondonne");
            }
            
            setGraphic(content);
        }
    }
}
