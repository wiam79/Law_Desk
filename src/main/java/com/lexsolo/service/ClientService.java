package com.lexsolo.service;

import com.lexsolo.dao.ClientDAO;

import com.lexsolo.entity.Client;
import java.util.List;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public void addClient(Client client) {
        // Business validation
        clientDAO.add(client);
    }

    public void updateClient(Client client) {
        clientDAO.update(client);
    }

    public void deleteClient(Long id) {
        clientDAO.delete(id);
    }

    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }
    
    public List<Client> searchClients(String name) {
        return clientDAO.searchByName(name);
    }
}
