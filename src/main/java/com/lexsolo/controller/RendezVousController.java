package com.lexsolo.controller;

import com.lexsolo.App;
import com.lexsolo.entity.Client;
import com.lexsolo.entity.Dossier;
import com.lexsolo.entity.RendezVous;
import com.lexsolo.service.ClientService;
import com.lexsolo.service.DossierService;
import com.lexsolo.service.RendezVousService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RendezVousController {

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private TableView<RendezVous> rdvTable;
    @FXML private TableColumn<RendezVous, Long> colId;
    @FXML private TableColumn<RendezVous, String> colDate;
    @FXML private TableColumn<RendezVous, String> colMotif;
    @FXML private TableColumn<RendezVous, String> colClient;
    @FXML private TableColumn<RendezVous, String> colDossier;

    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField motifField;
    @FXML private ComboBox<Client> clientCombo;
    @FXML private ComboBox<Dossier> dossierCombo;

    private final RendezVousService rdvService = new RendezVousService();
    private final ClientService clientService = new ClientService();
    private final DossierService dossierService = new DossierService();
    private final ObservableList<RendezVous> rdvList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateHeure().format(formatter)));
        
        colClient.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getClient() != null ? 
            cellData.getValue().getClient().getNom() + " " + cellData.getValue().getClient().getPrenom() : ""
        ));
        
        colDossier.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getDossier() != null ? 
            cellData.getValue().getDossier().getDescription() : "N/A"
        ));

        loadRendezVous();
        loadClients();
        loadDossiers();

        rdvTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
        
        setupClientConverter();
        setupDossierConverter();
    }
    
    private void setupClientConverter() {
        clientCombo.setConverter(new StringConverter<Client>() {
            @Override
            public String toString(Client c) {
                return c == null ? null : c.getNom() + " " + c.getPrenom();
            }
            @Override
            public Client fromString(String string) {
                return null; // Not needed
            }
        });
    }
    
    private void setupDossierConverter() {
        dossierCombo.setConverter(new StringConverter<Dossier>() {
            @Override
            public String toString(Dossier d) {
                return d == null ? null : d.getDescription();
            }
            @Override
            public Dossier fromString(String string) {
                return null;
            }
        });
    }

    private void loadRendezVous() {
        rdvList.setAll(rdvService.getAllRendezVous());
        rdvTable.setItems(rdvList);
    }
    
    private void loadClients() {
        clientCombo.setItems(FXCollections.observableArrayList(clientService.getAllClients()));
    }
    
    private void loadDossiers() {
        dossierCombo.setItems(FXCollections.observableArrayList(dossierService.getAllDossiers()));
    }

    private void populateForm(RendezVous rdv) {
        motifField.setText(rdv.getMotif());
        datePicker.setValue(rdv.getDateHeure().toLocalDate());
        timeField.setText(rdv.getDateHeure().toLocalTime().toString()); // Simple toString for now
        
        // Select client
        if (rdv.getClient() != null) {
            for (Client c : clientCombo.getItems()) {
                if (c.getId().equals(rdv.getClient().getId())) {
                    clientCombo.setValue(c);
                    break;
                }
            }
        }
        
        // Select dossier
        if (rdv.getDossier() != null) {
             for (Dossier d : dossierCombo.getItems()) {
                if (d.getId().equals(rdv.getDossier().getId())) {
                    dossierCombo.setValue(d);
                    break;
                }
            }
        } else {
            dossierCombo.setValue(null);
        }
    }

    @FXML
    private void handleAdd() {
        try {
            RendezVous rdv = new RendezVous();
            rdv.setMotif(motifField.getText());
            
            LocalDate date = datePicker.getValue();
            String timeStr = timeField.getText();
            if (date == null || timeStr == null || timeStr.isEmpty()) {
                 showAlert("Erreur", "Date et Heure (HH:mm) obligatoires.", Alert.AlertType.WARNING);
                 return;
            }
            
            try {
                LocalTime time = LocalTime.parse(timeStr); // Format HH:mm
                rdv.setDateHeure(LocalDateTime.of(date, time));
            } catch (Exception e) {
                 showAlert("Erreur", "Format de l'heure invalide. Utilisez HH:mm (ex: 14:30).", Alert.AlertType.ERROR);
                 return;
            }
            
            rdv.setClient(clientCombo.getValue());
            rdv.setDossier(dossierCombo.getValue());
            
            rdvService.addRendezVous(rdv);
            loadRendezVous();
            handleClear();
            showAlert("Succès", "Rendez-vous ajouté.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            RendezVous selected = rdvTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setMotif(motifField.getText());
                
                LocalDate date = datePicker.getValue();
                String timeStr = timeField.getText();
                
                 if (date == null || timeStr == null || timeStr.isEmpty()) {
                     showAlert("Erreur", "Date et Heure (HH:mm) obligatoires.", Alert.AlertType.WARNING);
                     return;
                }

                try {
                    LocalTime time = LocalTime.parse(timeStr);
                    selected.setDateHeure(LocalDateTime.of(date, time));
                } catch (Exception e) {
                     showAlert("Erreur", "Format de l'heure invalide. Utilisez HH:mm (ex: 14:30).", Alert.AlertType.ERROR);
                     return;
                }
                
                selected.setClient(clientCombo.getValue());
                selected.setDossier(dossierCombo.getValue());
                
                rdvService.updateRendezVous(selected);
                rdvTable.refresh();
                handleClear();
                showAlert("Succès", "Rendez-vous mis à jour.", Alert.AlertType.INFORMATION);
            } else {
                 showAlert("Attention", "Aucun rendez-vous sélectionné.", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur de mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        RendezVous selected = rdvTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                rdvService.deleteRendezVous(selected.getId());
                loadRendezVous();
                handleClear();
                showAlert("Succès", "Rendez-vous supprimé.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                 showAlert("Erreur", "Erreur de suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                 e.printStackTrace();
            }
        } else {
             showAlert("Attention", "Aucun rendez-vous sélectionné.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleClear() {
        motifField.clear();
        datePicker.setValue(null);
        timeField.clear();
        clientCombo.setValue(null);
        dossierCombo.setValue(null);
        rdvTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        try {
            App.setRoot("DashboardView");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
