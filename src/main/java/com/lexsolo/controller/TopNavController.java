package com.lexsolo.controller;

import com.lexsolo.App;
import javafx.fxml.FXML;

public class TopNavController {

    @FXML
    private void goToHome() {
        try {
            App.setRoot("DashboardView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToClients() {
        try {
            App.setRoot("ClientView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDossiers() {
        try {
            App.setRoot("DossierView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRendezVous() {
        try {
            App.setRoot("RendezVousView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Here you would clear session data if any
            App.setRoot("LoginView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

