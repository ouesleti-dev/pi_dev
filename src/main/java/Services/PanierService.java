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
        // Utiliser une requête qui laisse la base de données gérer l'auto-incrémentation et le timestamp
        String req = "INSERT INTO panier (id_events, prix, quantite, prix_total, statut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, panier.getId_events());
            ps.setInt(2, panier.getPrix());
            ps.setInt(3, panier.getQuantite());
            ps.setInt(4, panier.getPrix_total());
            ps.setString(5, panier.getStatut().toString());

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

    @Override
    public void Update(Panier panier) throws Exception {
        String req = "UPDATE panier SET id_events=?, prix=?, quantite=?, prix_total=?, statut=? WHERE id_panier=?";
        try (PreparedStatement ps = conn.prepareStatement(req)) {
            ps.setInt(1, panier.getId_events());
            ps.setInt(2, panier.getPrix());
            ps.setInt(3, panier.getQuantite());
            ps.setInt(4, panier.getPrix_total());
            ps.setString(5, panier.getStatut().toString());
            ps.setInt(6, panier.getId_panier());

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
                        try {
                            panier.setStatut(Panier.Statut.valueOf(statutStr));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Statut invalide dans la base de données: " + statutStr);
                            panier.setStatut(Panier.Statut.ABONDONNE); // Valeur par défaut
                        }
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
}