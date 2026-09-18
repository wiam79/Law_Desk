package com.lexsolo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nomComplet;
    
    @Column(unique = true)
    private String email;
    
    private String telephone;

    // Role could be added here if needed, but Secretaire subclass handles strict typing
    // private String role; 
}
