package com.example.backend.dto;

import com.example.backend.model.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

// Corpo de POST/PUT /orders. Todos os campos são opcionais porque o front envia:
//  - criar/modificar dados:  { clientId, carId, inicio, fim, credito }
//  - aprovar/reprovar/contratar/cancelar: { status, parecer }
// A obrigatoriedade de cada campo é validada no OrderService, conforme a operação.
// Campos extras enviados pelo front (ex.: criadoEm) são ignorados.
public record OrderRequest(
        UUID clientId,
        UUID carId,
        LocalDate inicio,
        LocalDate fim,
        CreditDto credito,
        OrderStatus status,
        String parecer) {}