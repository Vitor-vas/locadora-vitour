package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter @Setter @NoArgsConstructor
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false) private String rg;
    @Column(nullable = false, unique = true) private String cpf;
    @Column(nullable = false) private String nome;
    @Column(nullable = false) private String endereco;
    @Column(nullable = false) private String profissao;

    // Regra do enunciado: no máximo 3 entidades empregadoras.
    @ElementCollection
    @CollectionTable(name = "client_employers", joinColumns = @JoinColumn(name = "client_id"))
    @Size(max = 3)
    private List<Employer> empregadores = new ArrayList<>();
}
