package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// Nome "AppUser" e tabela "users": "user" é palavra reservada no PostgreSQL.
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash; // sempre BCrypt, nunca senha pura

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AgentType agentType; // preenchido só quando role = AGENTE

    @OneToOne @JoinColumn(name = "client_id")
    private Client client;       // preenchido só quando role = CLIENTE
}
