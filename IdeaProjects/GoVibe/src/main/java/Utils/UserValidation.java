package Utils;

import Models.User;
import java.util.regex.Pattern;

public class UserValidation {
    // Regex patterns
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^[0-9]{8}$";
    private static final String NAME_PATTERN = "^[A-Za-z\\s]{3,20}$";
    private static final String PASSWORD_PATTERN = "^.{8,}$";

    // Validation du nom
    public static boolean isValidName(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (!Pattern.matches(NAME_PATTERN, nom)) {
            throw new IllegalArgumentException("Le nom doit contenir entre 2 et 30 caractères alphabétiques");
        }
        return true;
    }

    // Validation du prénom
    public static boolean isValidPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        if (!Pattern.matches(NAME_PATTERN, prenom)) {
            throw new IllegalArgumentException("Le prénom doit contenir entre 2 et 30 caractères alphabétiques");
        }
        return true;
    }

    // Validation de l'email
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        return true;
    }

    // Validation du numéro de téléphone
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone ne peut pas être vide");
        }
        if (!Pattern.matches(PHONE_PATTERN, phone)) {
            throw new IllegalArgumentException("Le numéro de téléphone doit contenir 8 chiffres");
        }
        return true;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères");
        }
        return true;
    }



}