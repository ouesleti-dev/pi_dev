package Services;

import Models.Panier;
import Utils.MyDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PanierService implements IService<Panier> {
    Connection conn;

    public PanierService() {
        this.conn = MyDb.getInstance().getConn();
    }

    @Override
    public void Create(Panier panier) throws Exception {
        // Vérifier si l'événement existe déjà dans le panier
        Panier existingPanier = findPanierByEventId(panier.getId_events());

        if (existingPanier != null) {
            // Si l'événement existe déjà, incrémenter la quantité
            existingPanier.setQuantite(existingPanier.getQuantite() + 1);
            // Mettre à jour le prix total
            existingPanier.setPrix_total(existingPanier.getPrix() * existingPanier.getQuantite());
            // Mettre à jour le panier dans la base de données
            Update(existingPanier);

            // Mettre à jour l'objet panier passé en paramètre pour qu'il reflète les modifications
            panier.setId_panier(existingPanier.getId_panier());
            panier.setQuantite(existingPanier.getQuantite());
            panier.setPrix_total(existingPanier.getPrix_total());
            panier.setDate_creation(existingPanier.getDate_creation());
            panier.setStatut(existingPanier.getStatut());

            System.out.println("Quantité incrémentée pour l'événement " + panier.getId_events() + ". Nouvelle quantité: " + panier.getQuantite());
        } else {
            // Si l'événement n'existe pas encore dans le panier, créer une nouvelle entrée
            // Utiliser une requête qui laisse la base de données gérer l'auto-incrémentation et le timestamp
            // Omettre le statut pour éviter les problèmes de troncature
            String req = "INSERT INTO panier (id_events, prix, quantite, prix_total) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, panier.getId_events());
                ps.setInt(2, panier.getPrix());
                ps.setInt(3, panier.getQuantite());
                ps.setInt(4, panier.getPrix_total());

                ps.executeUpdate();

                // Récupérer l'ID auto-généré
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        panier.setId_panier(generatedKeys.getInt(1));
                        System.out.println("Panier ajouté avec succès! ID: " + panier.getId_panier());

                        // Récupérer la date de création générée par la base de données
                        String dateQuery = "SELECT date_creation FROM panier WHERE id_panier = ?";
                        try (PreparedStatement datePs = conn.prepareStatement(dateQuery)) {
                            datePs.setInt(1, panier.getId_panier());
                            try (ResultSet rs = datePs.executeQuery()) {
                                if (rs.next()) {
                                    panier.setDate_creation(rs.getTimestamp("date_creation"));
                                    System.out.println("Date de création: " + panier.getDate_creation());
                                }
                            }
                        }
                    } else {
                        throw new Exception("Échec de la création du panier, aucun ID généré.");
                    }
                }
            }
        }
    }

    @Override
    public void Update(Panier panier) throws Exception {
        // Omettre le statut pour éviter les problèmes de troncature
        String req = "UPDATE panier SET id_events=?, prix=?, quantite=?, prix_total=? WHERE id_panier=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, panier.getId_events());
            ps.setInt(2, panier.getPrix());
            ps.setInt(3, panier.getQuantite());
            ps.setInt(4, panier.getPrix_total());
            ps.setInt(5, panier.getId_panier());

            ps.executeUpdate();
        }
    }

    @Override
    public List<Panier> Display() throws Exception {
        List<Panier> paniers = new ArrayList<>();
        String req = "SELECT * FROM panier";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(req)) {

            while (rs.next()) {
                try {
                    Panier panier = new Panier(
                            rs.getInt("id_events"),
                            rs.getInt("prix"),
                            rs.getInt("quantite")
                    );
                    panier.setId_panier(rs.getInt("id_panier"));

                    // Gérer le cas où le statut est null ou invalide
                    String statutStr = rs.getString("statut");
                    if (statutStr != null && !statutStr.isEmpty()) {
                        // Utiliser la méthode fromCode pour convertir le code en énumération
                        panier.setStatut(Panier.Statut.fromCode(statutStr));
                    } else {
                        panier.setStatut(Panier.Statut.ABONDONNE); // Valeur par défaut
                    }

                    panier.setDate_creation(rs.getTimestamp("date_creation"));
                    paniers.add(panier);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la lecture d'un panier: " + e.getMessage());
                    // Continuer avec le panier suivant
                }
            }
        }
        return paniers;
    }

    @Override
    public void Delete() throws Exception {
        // Cette méthode est gardée pour respecter l'interface
    }

    // Méthode pour supprimer un panier spécifique par son ID
    public void DeleteById(int id) throws Exception {
        String req = "DELETE FROM panier WHERE id_panier = ?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Panier supprimé avec succès!");
        }
    }

    /**
     * Recherche un panier par l'ID de l'événement
     * @param eventId L'ID de l'événement à rechercher
     * @return Le panier contenant l'événement, ou null si aucun panier ne contient cet événement
     * @throws Exception En cas d'erreur lors de la recherche
     */
    public Panier findPanierByEventId(int eventId) throws Exception {
        String req = "SELECT * FROM panier WHERE id_events = ? AND statut = 'A' LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Panier panier = new Panier(
                            rs.getInt("id_events"),
                            rs.getInt("prix"),
                            rs.getInt("quantite")
                    );
                    panier.setId_panier(rs.getInt("id_panier"));

                    // Gérer le cas où le statut est null ou invalide
                    String statutStr = rs.getString("statut");
                    if (statutStr != null && !statutStr.isEmpty()) {
                        // Utiliser la méthode fromCode pour convertir le code en énumération
                        panier.setStatut(Panier.Statut.fromCode(statutStr));
                    } else {
                        panier.setStatut(Panier.Statut.ABONDONNE); // Valeur par défaut
                    }

                    panier.setDate_creation(rs.getTimestamp("date_creation"));
                    return panier;
                }
            }
        }
        return null;
    }
}