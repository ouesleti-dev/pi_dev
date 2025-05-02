import Models.Panier;
import Services.PanierService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestPanier {

    @Test
    public void testAutoIncrementAndTimestamp() {
        try {
            // Créer un nouveau panier
            Panier panier = new Panier(1, 100, 2);
            PanierService panierService = new PanierService();
            
            // Vérifier que l'ID est 0 avant l'insertion
            assertEquals(0, panier.getId_panier());
            
            // Insérer le panier dans la base de données
            panierService.Create(panier);
            
            // Vérifier que l'ID a été auto-incrémenté
            assertTrue(panier.getId_panier() > 0);
            
            // Vérifier que la date de création a été définie
            assertNotNull(panier.getDate_creation());
            
            System.out.println("Panier créé avec ID: " + panier.getId_panier());
            System.out.println("Date de création: " + panier.getDate_creation());
            
            // Nettoyer la base de données
            panierService.DeleteById(panier.getId_panier());
            
        } catch (Exception e) {
            fail("Exception lors du test: " + e.getMessage());
        }
    }
}
