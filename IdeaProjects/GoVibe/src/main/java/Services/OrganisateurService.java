package Services;

import Models.Evenement;
import Models.Organisateur;
import java.util.List;

public interface OrganisateurService {

    // Ajouter un organisateur
    void ajouterOrganisateur(Organisateur organisateur);

    // Lister tous les organisateurs
    List<Organisateur> listerOrganisateurs();

    // Rechercher un organisateur par son ID
    Organisateur rechercherOrganisateurParId(int id);

    // Lister tous les événements d'un organisateur
    List<Evenement> listerEvenementsParOrganisateur(int organisateurId);
}
