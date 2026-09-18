package com.lexsolo.controller;

import com.lexsolo.entity.Dossier;
import com.lexsolo.entity.RendezVous;
import com.lexsolo.service.ClientService;
import com.lexsolo.service.DossierService;
import com.lexsolo.service.RendezVousService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label clientCountLabel;
    @FXML private Label dossierCountLabel;
    @FXML private Label rdvCountLabel;
    
    @FXML private VBox rdvListContainer;
    @FXML private VBox urgentListContainer;

    private final ClientService clientService;
    private final DossierService dossierService;
    private final RendezVousService rendezVousService;

    public DashboardController() {
        this.clientService = new ClientService();
        this.dossierService = new DossierService();
        this.rendezVousService = new RendezVousService();
    }

    @FXML
    public void initialize() {
        loadStats();
    }

    private void loadStats() {
        try {
            List<RendezVous> allRdvs = rendezVousService.getAllRendezVous();
            List<Dossier> allDossiers = dossierService.getAllDossiers();
            
            // Counts
            if (clientCountLabel != null) clientCountLabel.setText(String.valueOf(clientService.getAllClients().size()));
            if (dossierCountLabel != null) dossierCountLabel.setText(String.valueOf(allDossiers.size()));
            if (rdvCountLabel != null) rdvCountLabel.setText(String.valueOf(allRdvs.size()));

            // RDV du Jour
            if (rdvListContainer != null) {
                rdvListContainer.getChildren().clear();
                LocalDate today = LocalDate.now();
                List<RendezVous> todaysRdvs = allRdvs.stream()
                    .filter(r -> r.getDateHeure() != null && r.getDateHeure().toLocalDate().equals(today))
                    .collect(Collectors.toList());

                if (todaysRdvs.isEmpty()) {
                    Label placeholder = new Label("Aucun rendez-vous aujourd'hui");
                    placeholder.setStyle("-fx-text-fill: #6b7280;");
                    rdvListContainer.getChildren().add(placeholder);
                } else {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    for (RendezVous rdv : todaysRdvs) {
                        String time = rdv.getDateHeure().format(timeFormatter);
                        String clientName = rdv.getClient() != null ? rdv.getClient().getNom() : "Client inconnu";
                        Label rdvLabel = new Label("🕒 " + time + " - " + clientName + " (" + rdv.getMotif() + ")");
                        rdvLabel.setStyle("-fx-text-fill: #1f2937; -fx-font-weight: bold;");
                        rdvListContainer.getChildren().add(rdvLabel);
                    }
                }
            }

            // Dossiers En Cours (was Urgents)
            if (urgentListContainer != null) {
                urgentListContainer.getChildren().clear();
                List<Dossier> enCoursDossiers = allDossiers.stream()
                    .filter(d -> d.getStatus() == Dossier.DossierStatus.EN_COURS)
                    .collect(Collectors.toList());

                if (enCoursDossiers.isEmpty()) {
                    Label placeholder = new Label("Aucun dossier en cours");
                    placeholder.setStyle("-fx-text-fill: #6b7280;");
                    urgentListContainer.getChildren().add(placeholder);
                } else {
                    for (Dossier dossier : enCoursDossiers) {
                        String clientName = dossier.getClient() != null ? dossier.getClient().getNom() : "Client inconnu";
                        Label dossierLabel = new Label("📁 " + dossier.getDescription() + " - " + clientName);
                        dossierLabel.setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;"); // Orange color
                        urgentListContainer.getChildren().add(dossierLabel);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


