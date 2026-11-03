package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Contrato de crédito do pedido. Mesmo formato do campo "credito" do front: { bancoId, valor }.
public record CreditDto(UUID bancoId, BigDecimal valor) {}