-- Ajouter la colonne is_banned à la table user
ALTER TABLE user ADD COLUMN is_banned BOOLEAN DEFAULT 0;

-- Mettre à jour les utilisateurs existants avec is_banned = false
UPDATE user SET is_banned = 0 WHERE is_banned IS NULL;
