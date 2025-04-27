package Services;

import Models.Evenement;
import Utils.GestionEvenement;
import java.util.List;

public class EvenementServiceImpl implements Service.EvenementService {

    private GestionEvenement gestionEvenement;

    public EvenementServiceImpl() {
        this.gestionEvenement = new GestionEvenement();  // Initialisation de la classe GestionEvenement
    }

    @Override
    public void ajouterEvenement(Evenement evenement) {
        gestionEvenement.ajouterEvenement(evenement);  // Appel à la méthode de GestionEvenement
    }

    @Override
    public List<Evenement> listerEvenements() {
        return gestionEvenement.listerEvenements();  // Retourne la liste des événements
    }

    @Override
    public Evenement chercherEvenementParId(int id) {
        return gestionEvenement.chercherEvenementParId(id);  // Retourne un événement par son ID
    }

    @Override
    public void supprimerEvenement(int id) {
        gestionEvenement.supprimerEvenement(id);  // Supprime un événement par son ID
    }

    @Override
    public List<Evenement> listerEvenementsParOrganisateur(int organisateurId) {
        return gestionEvenement.listerEvenementsParOrganisateur(organisateurId);  // Retourne les événements d'un organisateur spécifique
    }

    @Override
    public void mettreAJourEvenement(Evenement evenement) {
        gestionEvenement.mettreAJourEvenement(evenement);  // Met à jour un événement
    }
}
