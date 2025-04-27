package Service;

import Models.Evenement;
import java.util.List;

public interface EvenementService {

    // Ajouter un événement
    void ajouterEvenement(Evenement evenement);

    // Lister tous les événements
    List<Evenement> listerEvenements();

    // Rechercher un événement par ID
    Evenement chercherEvenementParId(int id);

    // Supprimer un événement
    void supprimerEvenement(int id);

    // Lister les événements d'un organisateur
    List<Evenement> listerEvenementsParOrganisateur(int organisateurId);

    // Mettre à jour un événement
    void mettreAJourEvenement(Evenement evenement);
}
