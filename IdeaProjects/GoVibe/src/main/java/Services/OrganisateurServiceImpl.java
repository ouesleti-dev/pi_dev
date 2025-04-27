package Services;

import Models.Evenement;
import Models.Organisateur;
import Utils.GestionEvenement;
import java.util.List;

public class OrganisateurServiceImpl implements OrganisateurService {

    private GestionEvenement gestionEvenement;

    // Constructeur de la classe
    public OrganisateurServiceImpl() {
        gestionEvenement = new GestionEvenement();
    }

    @Override
    public void ajouterOrganisateur(Organisateur organisateur) {
        gestionEvenement.ajouterOrganisateur(organisateur);
    }

    @Override
    public List<Organisateur> listerOrganisateurs() {
        return gestionEvenement.listerOrganisateurs();
    }

    @Override
    public Organisateur rechercherOrganisateurParId(int id) {
        return gestionEvenement.chercherOrganisateurParId(id);
    }

    @Override
    public List<Evenement> listerEvenementsParOrganisateur(int organisateurId) {
        return gestionEvenement.listerEvenementsParOrganisateur(organisateurId);
    }
}
