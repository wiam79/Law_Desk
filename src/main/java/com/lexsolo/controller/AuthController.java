package com.lexsolo.controller;

import com.lexsolo.App;
import com.lexsolo.entity.User;
import com.lexsolo.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    // ========== CHAMPS LOGIN VIEW ==========
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // ========== CHAMPS SIGNUP VIEW ==========
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private TextField telField;
    @FXML private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    // ========== MÉTHODE UTILITAIRE ==========

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ========== MÉTHODES LOGIN (LoginView.fxml) ==========

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom d'utilisateur et un mot de passe.", Alert.AlertType.WARNING);
            return;
        }

        try {
            User user = authService.signIn(username, password);
            if (user != null) {
                System.out.println("Login successful for user: " + user.getUsername());
                App.setRoot("DashboardView");
            } else {
                showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur de connexion", "Échec de la connexion : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSignup() {
        try {
            App.setRoot("SignupView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== MÉTHODES SIGNUP (SignupView.fxml) ==========

    @FXML
    private void handleRegister() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String tel = telField.getText();
        String username = usernameField.getText();
        String pwd = passwordField.getText();
        String confirmPwd = confirmPasswordField.getText();

        // Validation des champs vides
        if (nom.isEmpty() || email.isEmpty() || username.isEmpty() || pwd.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.WARNING);
            return;
        }

        // Validation de correspondance des mots de passe
        if (!pwd.equals(confirmPwd)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.", Alert.AlertType.ERROR);
            return;
        }

        // Création de l'objet User
        User user = new User();
        user.setNomComplet(nom);
        user.setEmail(email);
        user.setTelephone(tel);
        user.setUsername(username);
        user.setPassword(pwd);

        try {
            authService.signUp(user);
            showAlert("Succès", "Compte créé avec succès ! Vous pouvez maintenant vous connecter.", Alert.AlertType.INFORMATION);

            // Redirection vers Login
            App.setRoot("LoginView");
        } catch (Exception e) {
            showAlert("Erreur d'inscription", "Erreur lors de la création du compte : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.setRoot("LoginView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}