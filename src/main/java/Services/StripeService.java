package Services;

import com.stripe.Stripe;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.HashMap;
import java.util.Map;

public class StripeService {
    // Clés de test Stripe (ne pas utiliser en production)
    private static final String API_PUBLIC_KEY = "pk_test_51RMdw0CZSaQeTZHr6LBemzHMICOF7uN3qlB9Hv8emTXwFBPZUWwewRulrgGWRbUquMeRNpPrk7oNVqDtTi9VPIAX00ZgNBo9vm";
    private static final String API_SECRET_KEY = "sk_test_51RMdw0CZSaQeTZHrN8uGMLklaUXa5vGII4ZvSQ6z2nc7fulTT734GIjpNuBPl35rMOrVPId1wS0tuxI2ReAFliu200v9LdKyor";

    public StripeService() {
        // Initialiser la clé API Stripe
        Stripe.apiKey = API_SECRET_KEY;
    }

    /**
     * Crée une intention de paiement Stripe
     * @param amount Montant en centimes (ex: 1000 pour 10,00 €)
     * @param currency Devise (ex: "eur")
     * @param description Description du paiement
     * @return L'intention de paiement créée
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public PaymentIntent createPaymentIntent(long amount, String currency, String description) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    /**
     * Crée une session de paiement Stripe Checkout
     * @param amount Montant en centimes (ex: 1000 pour 10,00 €)
     * @param currency Devise (ex: "eur")
     * @param productName Nom du produit
     * @param successUrl URL de redirection en cas de succès
     * @param cancelUrl URL de redirection en cas d'annulation
     * @return La session de paiement créée
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public Session createCheckoutSession(long amount, String currency, String productName,
                                         String successUrl, String cancelUrl) throws StripeException {
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency)
                                .setUnitAmount(amount)
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(productName)
                                                .build()
                                )
                                .build()
                )
                .setQuantity(1L)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(lineItem)
                .build();

        return Session.create(params);
    }

    /**
     * Confirme un paiement Stripe
     * @param paymentIntentId ID de l'intention de paiement
     * @return L'intention de paiement confirmée
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public PaymentIntent confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method", "pm_card_visa"); // Méthode de paiement par défaut pour les tests

        return paymentIntent.confirm(params);
    }

    /**
     * Vérifie le statut d'un paiement
     * @param paymentIntentId ID de l'intention de paiement
     * @return true si le paiement est réussi, false sinon
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public boolean checkPaymentStatus(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return "succeeded".equals(paymentIntent.getStatus());
    }

    /**
     * Crée un paiement direct avec une carte de test
     * @param amount Montant en centimes
     * @param currency Devise
     * @param description Description du paiement
     * @return true si le paiement est réussi, false sinon
     * @throws StripeException En cas d'erreur avec l'API Stripe
     */
    public boolean processTestPayment(long amount, String currency, String description) throws StripeException {
        System.out.println("Début du traitement du paiement Stripe: " + description);
        System.out.println("Montant: " + amount + " centimes " + currency);

        try {
            // Utiliser l'API Stripe de manière plus simple
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount);
            params.put("currency", currency);
            params.put("description", description);

            // Utiliser une source de paiement de test directement
            params.put("source", "tok_visa"); // Token de carte Visa de test

            // Créer la charge directement (plus simple que PaymentIntent pour les tests)
            com.stripe.model.Charge charge = com.stripe.model.Charge.create(params);

            System.out.println("Charge créée avec ID: " + charge.getId());
            System.out.println("Statut de la charge: " + charge.getStatus());

            // Vérifier le statut
            boolean success = "succeeded".equals(charge.getStatus());
            System.out.println("Résultat du paiement: " + (success ? "Réussi" : "Échec"));

            return success;
        } catch (StripeException e) {
            System.err.println("Erreur Stripe: " + e.getMessage());
            throw e; // Relancer l'exception pour qu'elle soit gérée par l'appelant
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            throw new ApiException("Erreur inattendue lors du traitement du paiement", null, null, null, e);
        }
    }

    /**
     * Retourne la clé publique API Stripe
     * @return La clé publique API
     */
    public String getPublicKey() {
        return API_PUBLIC_KEY;
    }

    /**
     * Gère les erreurs Stripe et retourne un message d'erreur convivial
     * @param e L'exception Stripe
     * @return Un message d'erreur convivial
     */
    public String handleStripeError(StripeException e) {
        String errorMessage = "Erreur lors du traitement du paiement";

        // Analyser le type d'erreur
        if (e.getCode() != null) {
            switch (e.getCode()) {
                case "authentication_required":
                    errorMessage = "Authentification 3D Secure requise. Utilisez une carte de test sans 3D Secure.";
                    break;
                case "card_declined":
                    errorMessage = "Carte refusée. Utilisez une carte de test valide.";
                    break;
                case "expired_card":
                    errorMessage = "Carte expirée. Vérifiez la date d'expiration.";
                    break;
                case "incorrect_cvc":
                    errorMessage = "Code CVC incorrect.";
                    break;
                case "processing_error":
                    errorMessage = "Erreur lors du traitement de la carte. Veuillez réessayer.";
                    break;
                case "rate_limit":
                    errorMessage = "Trop de requêtes. Veuillez réessayer plus tard.";
                    break;
                case "invalid_request_error":
                    errorMessage = "Requête invalide. Vérifiez les paramètres de paiement.";
                    break;
                default:
                    errorMessage = "Erreur: " + e.getMessage();
            }
        }

        System.err.println("Erreur Stripe détaillée: " + e.getMessage());
        return errorMessage;
    }
}
