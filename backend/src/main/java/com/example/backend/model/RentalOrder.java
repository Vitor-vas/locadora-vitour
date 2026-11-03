package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

// Nome "RentalOrder" e tabela "rental_orders": "order" é palavra reservada no SQL.
@Entity
@Table(name = "rental_orders")
@Getter @Setter @NoArgsConstructor
public class RentalOrder {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false) @JoinColumn(name = "car_id")
    private Car car;

    @Column(nullable = false) private LocalDate inicio;
    @Column(nullable = false) private LocalDate fim;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDENTE;

    // Contrato de crédito (opcional): banco agente que concedeu e valor.
    @ManyToOne @JoinColumn(name = "credito_banco_id")
    private AppUser creditoBanco;
    @Column(precision = 12, scale = 2)
    private BigDecimal creditoValor;

    @Column(length = 1000)
    private String parecer; // parecer financeiro do agente

    @Column(nullable = false)
    private Instant criadoEm = Instant.now();
}
