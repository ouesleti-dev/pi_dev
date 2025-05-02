package Controllers;

import Models.Panier;
import Services.PanierService;
import Utils.MyDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class PanierController implements Initializable {

    @FXML
    private TextField txtcr;

    @FXML
    private TextField txtdc;

    @FXML
    private TextField txtidp;

    @FXML
    private TextField txttc;

    @FXML
    private TableView<Panier> tableView;

    @FXML
    private TableColumn<Panier, Integer> idEventsColumn;

    @FXML
    private TableColumn<Panier, Integer> prixColumn;

    @FXML
    private TableColumn<Panier, Integer> quantiteColumn;

    @FXML
    private TableColumn<Panier, Integer> prixTotalColumn;

    @FXML
    private TableColumn<Panier, String> statutColumn;

    private PanierService panierService;
    private ObservableList<Panier> panierList;

    public PanierController() {
        panierService = new PanierService();
        panierList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idEventsColumn.setCellValueFactory(new PropertyValueFactory<>("id_events"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        prixTotalColumn.setCellValueFactory(new PropertyValueFactory<>("prix_total"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        loadPanierData();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtidp.setText(String.valueOf(newSelection.getId_panier()));
                txtdc.setText(newSelection.getDate_creation().toString());
            }
        });
    }

    private void loadPanierData() {
        try {
            List<Panier> paniers = panierService.Display();
            panierList.clear();
            panierList.addAll(paniers);
            tableView.setItems(panierList);

            int total = paniers.stream().mapToInt(Panier::getPrix_total).sum();
            txttc.setText(String.valueOf(total));

            if (!panierList.isEmpty()) {
                tableView.getSelectionModel().selectFirst();
                Panier premierPanier = panierList.get(0);
                txtidp.setText(String.valueOf(premierPanier.getId_panier()));
                txtdc.setText(premierPanier.getDate_creation().toString());
            } else {
                txtidp.setText("");
                txtdc.setText("");
                txtcr.setText("");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données", e.getMessage());
        }
    }

    @FXML
    void Modifier(ActionEvent event) {
    }

    @FXML
    void Payer(ActionEvent event) {
        if (panierList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Aucun article dans le panier", "Votre panier est vide. Impossible de procéder au paiement.");
            return;
        }

        try {
            // Utiliser le total déjà affiché (qui peut inclure une réduction si elle a été validée)
            double totalFinal = Double.parseDouble(txttc.getText());

            for (Panier panier : panierList) {
                panier.setStatut(Panier.Statut.VALIDE);
                panierService.Update(panier);
            }

            loadPanierData();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement effectué",
                      "Votre commande a été validée avec succès.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du paiement", e.getMessage());
        }
    }

    @FXML
    void Supprimer(ActionEvent event) {
    }

    @FXML
    void Update(ActionEvent event) {
        loadPanierData();
    }

    @FXML
    void Valider(ActionEvent event) {
        String codeReduction = txtcr.getText().trim();

        if (codeReduction.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Code de réduction", "Code manquant",
                      "Veuillez entrer un code de réduction.");
            return;
        }

        if (panierList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Panier vide", "Aucun article dans le panier",
                      "Votre panier est vide. Impossible d'appliquer un code de réduction.");
            return;
        }

        if (codeReduction.equalsIgnoreCase("GOVIBE10")) {
            double reduction = 0.1; // 10% de réduction
            int totalSansReduction = panierList.stream().mapToInt(Panier::getPrix_total).sum();
            double totalAvecReduction = totalSansReduction * (1 - reduction);

            txttc.setText(String.format("%.2f", totalAvecReduction));

            showAlert(Alert.AlertType.INFORMATION, "Code de réduction", "La réduction est validée",
                      "Le code de réduction GOVIBE10 a été appliqué (10% de réduction).\n" +
                      "Total avant réduction: " + totalSansReduction + "\n" +
                      "Total après réduction: " + String.format("%.2f", totalAvecReduction));
        } else {
            showAlert(Alert.AlertType.WARNING, "Code de réduction", "Code invalide",
                      "Le code de réduction saisi n'est pas valide.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String showInputDialog(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        return dialog.showAndWait().orElse("");
    }

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
