package Services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service pour détecter et filtrer les mots inappropriés dans les textes.
 */
public class BadWordsService {

    // Liste de mots inappropriés (en français et anglais)
    private static final Set<String> BAD_WORDS = new HashSet<>(Arrays.asList(
            "idiot", "stupide", "con", "merde", "damn", "hell", "crap", "fool", "connard"
    ));

    /**
     * Vérifie si le texte contient des mots inappropriés.
     * @param text Le texte à vérifier
     * @return true si des mots inappropriés sont détectés, false sinon
     */
    public boolean containsBadWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        // Convertir en minuscules et diviser en mots
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");

        // Vérifier chaque mot
        for (String word : words) {
            if (BAD_WORDS.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remplace les mots inappropriés par des astérisques.
     * @param text Le texte à filtrer
     * @return Le texte filtré
     */
    public String filterBadWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String filteredText = text;

        for (String badWord : BAD_WORDS) {
            // Pattern pour correspondre au mot entier
            Pattern pattern = Pattern.compile("\\b" + badWord + "\\b", Pattern.CASE_INSENSITIVE);
            // Remplacer par des astérisques
            String replacement = "*".repeat(badWord.length());
            filteredText = pattern.matcher(filteredText).replaceAll(replacement);
        }

        return filteredText;
    }
}
