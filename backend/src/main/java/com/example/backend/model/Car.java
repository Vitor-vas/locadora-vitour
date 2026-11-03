package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter @Setter @NoArgsConstructor
public class Car {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true) private String matricula;
    @Column(nullable = false) private int ano;
    @Column(nullable = false) private String marca;
    @Column(nullable = false) private String modelo;
    @Column(nullable = false, unique = true) private String placa;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Owner proprietario; // CLIENTE, EMPRESA ou BANCO
}
