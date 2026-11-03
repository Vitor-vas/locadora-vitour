package com.example.backend.dto;

import com.example.backend.model.Employer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record EmployerDto(@NotBlank String entidade, @NotNull @PositiveOrZero BigDecimal rendimento) {
    public static EmployerDto from(Employer e) { return new EmployerDto(e.getEntidade(), e.getRendimento()); }

    public Employer toEntity() {
        Employer e = new Employer();
        e.setEntidade(entidade);
        e.setRendimento(rendimento);
        return e;
    }
}
