package Controllers;

import Models.Panier;
import Services.PanierService;
import Services.StripeService;
import Utils.MyDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import java.io.File;
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
    private TextField searchField;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label vatLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private ListView<Panier> panierListView;

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
        // Configurer la ListView avec une cellule personnalisée
        panierListView.setCellFactory(param -> new PanierListCell());

        loadPanierData();

        // Initialiser la barre de recherche en temps réel
        setupSearchField();

        panierListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtidp.setText(String.valueOf(newSelection.getId_panier()));
                txtdc.setText(newSelection.getDate_creation().toString());
            }
        });
    }

    private void setupSearchField() {
        // Créer une FilteredList wrapée autour de l'ObservableList
        FilteredList<Panier> filteredData = new FilteredList<>(panierList, p -> true);

        // Ajouter un listener au champ de recherche pour mettre à jour le filtre en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(panier -> {
                // Si le champ de recherche est vide, afficher tous les paniers
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Convertir le texte de recherche en minuscules pour une recherche insensible à la casse
                String lowerCaseFilter = newValue.toLowerCase();

                // Comparer les champs du panier avec le texte de recherche
                if (String.valueOf(panier.getId_panier()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur l'ID du panier
                } else if (String.valueOf(panier.getId_events()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur l'ID de l'événement
                } else if (String.valueOf(panier.getPrix()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur le prix
                } else if (String.valueOf(panier.getQuantite()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur la quantité
                } else if (String.valueOf(panier.getPrix_total()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur le prix total
                } else if (panier.getStatut() != null && panier.getStatut().toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Correspondance sur le statut
                }
                return false; // Pas de correspondance
            });

            // Mettre à jour la ListView avec les données filtrées
            panierListView.setItems(filteredData);

            // Mettre à jour le résumé en fonction des éléments filtrés
            updateSummary(filteredData);
        });
    }

    private void updateSummary(FilteredList<Panier> filteredData) {
        // Calculer le total des éléments filtrés
        int total = 0;
        for (Panier panier : filteredData) {
            total += panier.getPrix_total();
        }

        // Mettre à jour les labels de résumé
        txttc.setText(String.valueOf(total));
        subtotalLabel.setText(String.format("%.2f €", (double)total));

        // Calculer la TVA (4%)
        double vat = total * 0.04;
        vatLabel.setText(String.format("%.2f €", vat));

        // Calculer le total avec TVA
        double totalWithVat = total + vat;
        totalLabel.setText(String.format("%.2f €", totalWithVat));
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        // La recherche est déjà gérée par le listener sur le champ de recherche
        // Cette méthode est appelée lorsque l'utilisateur clique sur le bouton Rechercher
        System.out.println("Recherche en cours: " + searchField.getText());
    }

    private void loadPanierData() {
        try {
            List<Panier> paniers = panierService.Display();
            if (panierList == null) {
                panierList = FXCollections.observableArrayList();
            } else {
                panierList.clear();
            }
            panierList.addAll(paniers);
            panierListView.setItems(panierList);

            int total = paniers.stream().mapToInt(Panier::getPrix_total).sum();
            txttc.setText(String.valueOf(total));

            // Mettre à jour les labels de résumé
            subtotalLabel.setText(String.format("%.2f €", (double)total));

            // Calculer la TVA (4%)
            double vat = total * 0.04;
            vatLabel.setText(String.format("%.2f €", vat));

            // Calculer le total avec TVA
            double totalWithVat = total + vat;
            totalLabel.setText(String.format("%.2f €", totalWithVat));

            if (!panierList.isEmpty()) {
                panierListView.getSelectionModel().selectFirst();
                Panier premierPanier = panierList.get(0);
                txtidp.setText(String.valueOf(premierPanier.getId_panier()));
                if (premierPanier.getDate_creation() != null) {
                    txtdc.setText(premierPanier.getDate_creation().toString());
                } else {
                    txtdc.setText("Date non disponible");
                }
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
        Panier selectedPanier = panierListView.getSelectionModel().getSelectedItem();
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

    /**
     * Gère le clic sur le bouton de retour pour revenir à la liste des réservations
     * @param event L'événement de clic
     */
    @FXML
    void handleBackButton(ActionEvent event) {
        try {
            // Charger la page de liste des réservations
            File file = new File("src/main/resources/fxml/event/ReservationList.fxml");
            if (file.exists()) {
                URL url = file.toURI().toURL();
                FXMLLoader loader = new FXMLLoader(url);
                Parent root = loader.load();

                // Récupérer la scène actuelle
                Scene currentScene = panierListView.getScene();
                Stage stage = (Stage) currentScene.getWindow();

                // Remplacer la scène actuelle par la page de liste des réservations
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des réservations");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Fichier FXML non trouvé", file.getAbsolutePath());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void Payer(ActionEvent event) {
        if (panierList == null || panierList.isEmpty()) {
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
                    Panier selectedPanier = panierListView.getSelectionModel().getSelectedItem();
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
        Panier selectedPanier = panierListView.getSelectionModel().getSelectedItem();
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

            // Mettre à jour les labels de résumé
            subtotalLabel.setText(String.format("%.2f €", totalAvecReduction));

            // Calculer la TVA (4%)
            double vat = totalAvecReduction * 0.04;
            vatLabel.setText(String.format("%.2f €", vat));

            // Calculer le total avec TVA
            double totalWithVat = totalAvecReduction + vat;
            totalLabel.setText(String.format("%.2f €", totalWithVat));

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

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        showAlert(alertType, title, null, content);
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
