package com.lexsolo.service;

import com.lexsolo.dao.UserDAO;
import com.lexsolo.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service d'authentification gérant l'inscription et la connexion des utilisateurs
 * avec hashage sécurisé des mots de passe
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Inscrit un nouvel utilisateur dans le système
     *
     * @param user L'utilisateur à inscrire (le mot de passe sera hashé automatiquement)
     * @throws AuthenticationException Si le username ou l'email existe déjà
     * @throws ValidationException Si les données sont invalides
     */
    public void signUp(User user) throws AuthenticationException, ValidationException {
        LOGGER.info("Tentative d'inscription pour l'utilisateur: " + user.getUsername());

        try {
            // Validation des données
            validateUserData(user);

            // Vérification de l'unicité du username
            if (userDAO.findByUsername(user.getUsername()) != null) {
                LOGGER.warning("Tentative d'inscription avec un username existant: " + user.getUsername());
                throw new AuthenticationException("Ce nom d'utilisateur est déjà utilisé.");
            }

            // Vérification de l'unicité de l'email
            if (userDAO.findByEmail(user.getEmail()) != null) {
                LOGGER.warning("Tentative d'inscription avec un email existant: " + user.getEmail());
                throw new AuthenticationException("Cet email est déjà utilisé.");
            }

            // Hashage du mot de passe
            String hashedPassword = hashPassword(user.getPassword());
            user.setPassword(hashedPassword);

            // Sauvegarde de l'utilisateur
            userDAO.add(user);

            LOGGER.info("Utilisateur inscrit avec succès: " + user.getUsername());

        } catch (AuthenticationException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'inscription de l'utilisateur", e);
            throw new AuthenticationException("Une erreur est survenue lors de l'inscription. Veuillez réessayer.");
        }
    }

    /**
     * Authentifie un utilisateur avec son username et mot de passe
     *
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe (en clair)
     * @return L'utilisateur authentifié
     * @throws AuthenticationException Si les identifiants sont incorrects
     */
    public User signIn(String username, String password) throws AuthenticationException {
        LOGGER.info("Tentative de connexion pour l'utilisateur: " + username);

        try {
            // Validation des entrées
            if (username == null || username.trim().isEmpty()) {
                throw new AuthenticationException("Le nom d'utilisateur est requis.");
            }

            if (password == null || password.isEmpty()) {
                throw new AuthenticationException("Le mot de passe est requis.");
            }

            // Recherche de l'utilisateur
            User user = userDAO.findByUsername(username);

            if (user == null) {
                LOGGER.warning("Tentative de connexion avec un username inexistant: " + username);
                throw new AuthenticationException("Nom d'utilisateur ou mot de passe incorrect.");
            }

            // Vérification du mot de passe
            if (!verifyPassword(password, user.getPassword())) {
                LOGGER.warning("Tentative de connexion avec un mot de passe incorrect pour: " + username);
                throw new AuthenticationException("Nom d'utilisateur ou mot de passe incorrect.");
            }

            LOGGER.info("Connexion réussie pour l'utilisateur: " + username);
            return user;

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la connexion de l'utilisateur", e);
            throw new AuthenticationException("Une erreur est survenue lors de la connexion. Veuillez réessayer.");
        }
    }

    /**
     * Vérifie si un username est disponible
     *
     * @param username Le username à vérifier
     * @return true si le username est disponible, false sinon
     */
    public boolean isUsernameAvailable(String username) {
        try {
            return userDAO.findByUsername(username) == null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du username", e);
            return false;
        }
    }

    /**
     * Vérifie si un email est disponible
     *
     * @param email L'email à vérifier
     * @return true si l'email est disponible, false sinon
     */
    public boolean isEmailAvailable(String email) {
        try {
            return userDAO.findByEmail(email) == null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification de l'email", e);
            return false;
        }
    }

    // ==================== MÉTHODES PRIVÉES ====================

    /**
     * Hash un mot de passe en utilisant BCrypt
     *
     * @param password Le mot de passe en clair
     * @return Le mot de passe hashé
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un hash
     *
     * @param password Le mot de passe en clair
     * @param hashedPassword Le hash à vérifier
     * @return true si le mot de passe correspond, false sinon
     */
    private boolean verifyPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors de la vérification du mot de passe", e);
            return false;
        }
    }

    /**
     * Valide les données d'un utilisateur
     *
     * @param user L'utilisateur à valider
     * @throws ValidationException Si les données sont invalides
     */
    private void validateUserData(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Les données utilisateur sont requises.");
        }

        // Validation du username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new ValidationException("Le nom d'utilisateur est requis.");
        }

        if (user.getUsername().length() < 3) {
            throw new ValidationException("Le nom d'utilisateur doit contenir au moins 3 caractères.");
        }

        if (user.getUsername().length() > 50) {
            throw new ValidationException("Le nom d'utilisateur ne peut pas dépasser 50 caractères.");
        }

        if (!user.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores.");
        }

        // Validation de l'email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("L'email est requis.");
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Format d'email invalide.");
        }

        // Validation du mot de passe
        validatePassword(user.getPassword());

        // Validation du nom complet
        if (user.getNomComplet() != null && user.getNomComplet().length() > 100) {
            throw new ValidationException("Le nom complet ne peut pas dépasser 100 caractères.");
        }
    }

    /**
     * Valide un mot de passe
     *
     * @param password Le mot de passe à valider
     * @throws ValidationException Si le mot de passe est invalide
     */
    private void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Le mot de passe est requis.");
        }

        if (password.length() < 8) {
            throw new ValidationException("Le mot de passe doit contenir au moins 8 caractères.");
        }

        if (password.length() > 100) {
            throw new ValidationException("Le mot de passe ne peut pas dépasser 100 caractères.");
        }

        // Vérification de la complexité
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasUpper || !hasLower || !hasDigit) {
            throw new ValidationException(
                    "Le mot de passe doit contenir au moins une majuscule, une minuscule et un chiffre."
            );
        }
    }

    // ==================== CLASSES D'EXCEPTIONS ====================

    /**
     * Exception levée lors d'une erreur d'authentification
     */
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception levée lors d'une erreur de validation
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}