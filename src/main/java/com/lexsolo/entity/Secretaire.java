package com.lexsolo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "secretaires")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Secretaire extends User {
    // Specific fields for Secretaire if any
}
