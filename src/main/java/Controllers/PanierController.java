package Controllers;

import Models.Panier;
import Services.PanierService;
import Services.StripeService;
import Utils.MyDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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
    private StripeService stripeService;

    public PanierController() {
        panierService = new PanierService();
        panierList = FXCollections.observableArrayList();
        stripeService = new StripeService();
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
        Panier selectedPanier = tableView.getSelectionModel().getSelectedItem();
        if (selectedPanier == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Aucun article sélectionné",
                    "Veuillez sélectionner un article à modifier.");
            return;
        }

        // Demander la nouvelle quantité
        String nouvelleQuantiteStr = showInputDialog("Modifier la quantité",
                "Entrez la nouvelle quantité pour l'article (actuelle: " + selectedPanier.getQuantite() + "):");

        if (nouvelleQuantiteStr.isEmpty()) {
            return; // L'utilisateur a annulé
        }

        try {
            int nouvelleQuantite = Integer.parseInt(nouvelleQuantiteStr);
            if (nouvelleQuantite <= 0) {
                showAlert(Alert.AlertType.WARNING, "Quantité invalide", "La quantité doit être positive",
                        "Veuillez entrer une quantité supérieure à zéro.");
                return;
            }

            // Mettre à jour la quantité et le prix total
            selectedPanier.setQuantite(nouvelleQuantite);
            selectedPanier.setPrix_total(selectedPanier.getPrix() * nouvelleQuantite);

            // Mettre à jour dans la base de données
            panierService.Update(selectedPanier);

            // Rafraîchir les données
            loadPanierData();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Article modifié",
                    "La quantité a été mise à jour avec succès.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide",
                    "Veuillez entrer un nombre entier valide.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification", e.getMessage());
        }
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

            // Créer une boîte de dialogue pour choisir le mode de paiement
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Mode de paiement");
            alert.setHeaderText("Choisissez votre mode de paiement");
            alert.setContentText("Comment souhaitez-vous payer ?");

            // Créer les boutons personnalisés
            ButtonType payerEnLigneBtn = new ButtonType("Payer en ligne");
            ButtonType payerLivraisonBtn = new ButtonType("Payer avec livraison");
            ButtonType annulerBtn = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

            // Ajouter les boutons à la boîte de dialogue
            alert.getButtonTypes().setAll(payerEnLigneBtn, payerLivraisonBtn, annulerBtn);

            // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
            ButtonType result = alert.showAndWait().orElse(annulerBtn);

            if (result == annulerBtn) {
                return; // L'utilisateur a annulé
            }

            // Traiter le mode de paiement choisi
            if (result == payerEnLigneBtn) {
                // Paiement en ligne avec Stripe
                try {
                    // Ouvrir le formulaire de paiement en ligne
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Authentification/PaiementEnLigneForm.fxml"));
                    Parent root = loader.load();

                    // Configurer le contrôleur du formulaire de paiement
                    PaiementEnLigneController paiementController = loader.getController();
                    paiementController.setMontantTotal(totalFinal);

                    // Si un panier est sélectionné, utiliser ses informations
                    Panier selectedPanier = tableView.getSelectionModel().getSelectedItem();
                    if (selectedPanier != null) {
                        paiementController.setPanierId(selectedPanier.getId_panier());
                        paiementController.setDateCreation(selectedPanier.getDate_creation());
                    } else if (!panierList.isEmpty()) {
                        // Sinon utiliser le premier panier de la liste
                        paiementController.setPanierId(panierList.get(0).getId_panier());
                        paiementController.setDateCreation(panierList.get(0).getDate_creation());
                    }

                    paiementController.setPanierList(panierList);

                    // Créer et configurer la fenêtre
                    Stage paiementStage = new Stage();
                    paiementStage.setTitle("Paiement en ligne");
                    paiementStage.setScene(new Scene(root));
                    paiementStage.initModality(Modality.APPLICATION_MODAL); // Bloque l'interaction avec la fenêtre principale

                    // Afficher la fenêtre et attendre qu'elle soit fermée
                    paiementStage.showAndWait();

                    // Vérifier si le paiement a été confirmé
                    if (paiementController.isConfirmed()) {
                        // Mettre à jour le statut des paniers
                        for (Panier panier : panierList) {
                            panier.setStatut(Panier.Statut.VALIDE);
                            panierService.Update(panier);
                        }

                        // Rafraîchir les données
                        loadPanierData();
                    }
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de paiement", e.getMessage());
                }
            } else {
                // Paiement avec livraison - Ouvrir le formulaire de livraison
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Authentification/LivraisonForm.fxml"));
                    Parent root = loader.load();

                    // Configurer le contrôleur du formulaire de livraison
                    LivraisonController livraisonController = loader.getController();
                    livraisonController.setMontantTotal(totalFinal);

                    // Créer et configurer la fenêtre
                    Stage livraisonStage = new Stage();
                    livraisonStage.setTitle("Informations de livraison");
                    livraisonStage.setScene(new Scene(root));
                    livraisonStage.initModality(Modality.APPLICATION_MODAL); // Bloque l'interaction avec la fenêtre principale

                    // Afficher la fenêtre et attendre qu'elle soit fermée
                    livraisonStage.showAndWait();

                    // Vérifier si la commande a été confirmée
                    if (livraisonController.isConfirmed()) {
                        // Mettre à jour le statut des paniers
                        for (Panier panier : panierList) {
                            panier.setStatut(Panier.Statut.VALIDE);
                            panierService.Update(panier);
                        }

                        // Rafraîchir les données
                        loadPanierData();
                    }
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de livraison", e.getMessage());
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du paiement", e.getMessage());
        }
    }

    @FXML
    void Supprimer(ActionEvent event) {
        Panier selectedPanier = tableView.getSelectionModel().getSelectedItem();
        if (selectedPanier == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Aucun article sélectionné",
                    "Veuillez sélectionner un article à supprimer.");
            return;
        }

        boolean confirmed = showConfirmationDialog("Confirmation de suppression",
                "Êtes-vous sûr de vouloir supprimer cet article du panier ?");

        if (confirmed) {
            try {
                panierService.DeleteById(selectedPanier.getId_panier());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Article supprimé",
                        "L'article a été supprimé du panier avec succès.");
                loadPanierData(); // Rafraîchir les données
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        }
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
