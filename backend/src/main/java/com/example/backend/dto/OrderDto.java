package com.example.backend.dto;

import com.example.backend.model.OrderStatus;
import com.example.backend.model.RentalOrder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

// Resposta de pedido, no mesmo formato do tipo Order do front.
public record OrderDto(
        UUID id,
        UUID clientId,
        UUID carId,
        LocalDate inicio,
        LocalDate fim,
        OrderStatus status,
        CreditDto credito,
        String parecer,
        Instant criadoEm) {

    public static OrderDto from(RentalOrder o) {
        CreditDto credito = o.getCreditoBanco() == null ? null
                : new CreditDto(o.getCreditoBanco().getId(), o.getCreditoValor());
        return new OrderDto(o.getId(), o.getClient().getId(), o.getCar().getId(), o.getInicio(), o.getFim(),
                o.getStatus(), credito, o.getParecer(), o.getCriadoEm());
    }
}