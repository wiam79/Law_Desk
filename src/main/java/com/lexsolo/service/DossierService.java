package com.lexsolo.service;

import com.lexsolo.dao.DossierDAO;

import com.lexsolo.entity.Dossier;
import java.util.List;

public class DossierService {
    private final DossierDAO dossierDAO;

    public DossierService() {
        this.dossierDAO = new DossierDAO();
    }

    public void addDossier(Dossier dossier) {
        dossierDAO.add(dossier);
    }

    public void updateDossier(Dossier dossier) {
        dossierDAO.update(dossier);
    }

    public void deleteDossier(Long id) {
        dossierDAO.delete(id);
    }

    public List<Dossier> getAllDossiers() {
        return dossierDAO.findAll();
    }

    public List<Dossier> getDossiersByClient(Long clientId) {
        return dossierDAO.findByClientId(clientId);
    }
    


}
