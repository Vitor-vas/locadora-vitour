package com.example.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// @Embeddable: não tem tabela própria nem id; vira linhas na coleção do Client.
@Embeddable
@Getter @Setter @NoArgsConstructor
public class Employer {
    @Column(nullable = false)
    private String entidade;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rendimento;
}
