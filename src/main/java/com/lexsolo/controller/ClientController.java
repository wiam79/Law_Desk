package com.lexsolo.controller;

import com.lexsolo.App;
import com.lexsolo.entity.Client;
import com.lexsolo.service.ClientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;

public class ClientController {

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Long> colId;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colTel;

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telField;
    @FXML private TextField searchField;

    private final ClientService clientService = new ClientService();
    private final ObservableList<Client> clientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        loadClients();

        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void loadClients() {
        clientList.setAll(clientService.getAllClients());
        clientTable.setItems(clientList);
    }

    private void populateForm(Client client) {
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        emailField.setText(client.getEmail());
        telField.setText(client.getTelephone());
    }

    @FXML
    private void handleAdd() {
        try {
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()) {
                showAlert("Erreur", "Le nom et le prénom sont obligatoires.", Alert.AlertType.WARNING);
                return;
            }
            Client client = new Client();
            client.setNom(nomField.getText());
            client.setPrenom(prenomField.getText());
            client.setEmail(emailField.getText());
            client.setTelephone(telField.getText());

            clientService.addClient(client);
            loadClients();
            handleClear();
            showAlert("Succès", "Client ajouté avec succès.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ajouter le client: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setNom(nomField.getText());
                selected.setPrenom(prenomField.getText());
                selected.setEmail(emailField.getText());
                selected.setTelephone(telField.getText());

                clientService.updateClient(selected);
                clientTable.refresh();
                handleClear();
                showAlert("Succès", "Client mis à jour avec succès.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de mettre à jour le client: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un client à modifier.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleDelete() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                clientService.deleteClient(selected.getId());
                loadClients();
                handleClear();
                showAlert("Succès", "Client supprimé avec succès.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de supprimer le client: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un client à supprimer.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.isEmpty()) {
            loadClients();
        } else {
            clientList.setAll(clientService.searchClients(keyword));
            clientTable.setItems(clientList);
        }
    }

    @FXML
    private void handleClear() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telField.clear();
        clientTable.getSelectionModel().clearSelection();
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
