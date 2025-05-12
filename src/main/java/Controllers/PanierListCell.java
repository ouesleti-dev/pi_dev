package Controllers;

import Models.Panier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PanierListCell extends ListCell<Panier> {
    
    private HBox content;
    private VBox detailsBox;
    private Label titleLabel;
    private Label priceLabel;
    private Label detailsLabel;
    private Label statusLabel;
    
    public PanierListCell() {
        super();
        
        // Créer les composants
        titleLabel = new Label();
        titleLabel.getStyleClass().add("item-title");
        
        priceLabel = new Label();
        priceLabel.getStyleClass().add("item-price");
        
        detailsLabel = new Label();
        detailsLabel.getStyleClass().add("item-details");
        
        statusLabel = new Label();
        statusLabel.getStyleClass().add("item-status");
        
        // Organiser les détails dans une VBox
        detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(titleLabel, detailsLabel);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);
        
        // Créer le conteneur principal
        content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(detailsBox, statusLabel, priceLabel);
    }
    
    @Override
    protected void updateItem(Panier panier, boolean empty) {
        super.updateItem(panier, empty);
        
        if (empty || panier == null) {
            setGraphic(null);
        } else {
            // Définir les valeurs
            titleLabel.setText("Événement #" + panier.getId_events());
            detailsLabel.setText("Quantité: " + panier.getQuantite() + " | Date: " + 
                                (panier.getDate_creation() != null ? panier.getDate_creation().toString() : "N/A"));
            priceLabel.setText(panier.getPrix_total() + " €");
            
            // Définir le statut avec style
            statusLabel.setText(panier.getStatut().toString());
            statusLabel.getStyleClass().removeAll("status-valide", "status-abondonne");
            
            if (panier.getStatut() == Panier.Statut.VALIDE) {
                statusLabel.getStyleClass().add("status-valide");
            } else {
                statusLabel.getStyleClass().add("status-abondonne");
            }
            
            setGraphic(content);
        }
    }
}
