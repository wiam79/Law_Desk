package com.lexsolo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "dossiers")
@Data
@NoArgsConstructor
public class Dossier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(columnDefinition = "DATE")
    private LocalDate dateCreation;

    @Enumerated(EnumType.STRING)
    private DossierStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public enum DossierStatus {
        A_TRAITER,
        EN_COURS,
        CLOTURE
    }
}