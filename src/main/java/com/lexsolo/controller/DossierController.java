package com.lexsolo.controller;

import com.lexsolo.App;
import com.lexsolo.entity.Client;
import com.lexsolo.entity.Dossier;
import com.lexsolo.entity.Dossier.DossierStatus;
import com.lexsolo.service.ClientService;
import com.lexsolo.service.DossierService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class DossierController {

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private TableView<Dossier> dossierTable;
    @FXML private TableColumn<Dossier, Long> colId;
    @FXML private TableColumn<Dossier, String> colDesc;
    @FXML private TableColumn<Dossier, LocalDate> colDate;
    @FXML private TableColumn<Dossier, DossierStatus> colStatus;
    @FXML private TableColumn<Dossier, String> colClient;

    @FXML private TextField descField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<DossierStatus> statusCombo;
    @FXML private ComboBox<Client> clientCombo;
    @FXML private ComboBox<DossierStatus> filterStatusCombo;

    private final DossierService dossierService = new DossierService();
    private final ClientService clientService = new ClientService();
    private final ObservableList<Dossier> dossierList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colClient.setCellValueFactory(cellData -> {
            if (cellData.getValue().getClient() != null) {
                return new SimpleStringProperty(cellData.getValue().getClient().getNom() + " " + cellData.getValue().getClient().getPrenom());
            }
            return new SimpleStringProperty("");
        });

        statusCombo.setItems(FXCollections.observableArrayList(DossierStatus.values()));
        filterStatusCombo.setItems(FXCollections.observableArrayList(DossierStatus.values()));

        loadClients();
        loadDossiers();

        dossierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        // Custom StringConverter for Client ComboBox
        clientCombo.setConverter(new StringConverter<Client>() {
            @Override
            public String toString(Client client) {
                return client == null ? null : client.getNom() + " " + client.getPrenom();
            }

            @Override
            public Client fromString(String string) {
                return clientCombo.getItems().stream().filter(client ->
                        (client.getNom() + " " + client.getPrenom()).equals(string)).findFirst().orElse(null);
            }
        });
    }

    private void loadDossiers() {
        dossierList.setAll(dossierService.getAllDossiers());
        dossierTable.setItems(dossierList);
    }

    private void loadClients() {
        clientCombo.setItems(FXCollections.observableArrayList(clientService.getAllClients()));
    }

    private void populateForm(Dossier dossier) {
        descField.setText(dossier.getDescription());
        datePicker.setValue(dossier.getDateCreation());
        statusCombo.setValue(dossier.getStatus());

        if (dossier.getClient() != null) {
            for(Client c : clientCombo.getItems()) {
                if (c.getId().equals(dossier.getClient().getId())) {
                    clientCombo.setValue(c);
                    break;
                }
            }
        } else {
            clientCombo.setValue(null);
        }
    }

    @FXML
    private void handleAdd() {
        try {
            Dossier dossier = new Dossier();
            dossier.setDescription(descField.getText());
            dossier.setDateCreation(datePicker.getValue());
            dossier.setStatus(statusCombo.getValue());
            dossier.setClient(clientCombo.getValue());

            dossierService.addDossier(dossier);
            loadDossiers();
            handleClear();
            showAlert("Succès", "Dossier ajouté avec succès.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        Dossier selected = dossierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setDescription(descField.getText());
                selected.setDateCreation(datePicker.getValue());
                selected.setStatus(statusCombo.getValue());
                selected.setClient(clientCombo.getValue());

                dossierService.updateDossier(selected);
                dossierTable.refresh();
                handleClear();
                showAlert("Succès", "Dossier mis à jour.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Attention", "Aucun dossier sélectionné.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDelete() {
        Dossier selected = dossierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                dossierService.deleteDossier(selected.getId());
                loadDossiers();
                handleClear();
                showAlert("Succès", "Dossier supprimé.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur de suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Attention", "Aucun dossier sélectionné.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleFilter() {
        DossierStatus status = filterStatusCombo.getValue();
        if (status == null) {
            loadDossiers();
        } else {
            // Local filter for UI.
            dossierList.setAll(dossierService.getAllDossiers().stream()
                    .filter(d -> d.getStatus() == status)
                    .toList());
            dossierTable.setItems(dossierList);
        }
    }

    @FXML
    private void handleClear() {
        descField.clear();
        datePicker.setValue(null);
        statusCombo.setValue(null);
        clientCombo.setValue(null);
        dossierTable.getSelectionModel().clearSelection();
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