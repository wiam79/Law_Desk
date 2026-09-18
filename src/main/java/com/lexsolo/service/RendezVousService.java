package com.lexsolo.service;

import com.lexsolo.dao.RendezVousDAO;

import com.lexsolo.entity.RendezVous;
import java.util.List;

public class RendezVousService {
    private final RendezVousDAO rendezVousDAO;

    public RendezVousService() {
        this.rendezVousDAO = new RendezVousDAO();
    }

    public void addRendezVous(RendezVous rendezVous) {
        // Implement conflict detection logic here if needed
        rendezVousDAO.add(rendezVous);
    }

    public void updateRendezVous(RendezVous rendezVous) {
        rendezVousDAO.update(rendezVous);
    }

    public void deleteRendezVous(Long id) {
        rendezVousDAO.delete(id);
    }

    public List<RendezVous> getAllRendezVous() {
        return rendezVousDAO.findAll();
    }
}
