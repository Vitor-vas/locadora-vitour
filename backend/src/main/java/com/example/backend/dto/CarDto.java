package com.example.backend.dto;

import com.example.backend.model.Car;
import com.example.backend.model.Owner;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Mesmo formato JSON do tipo Car do front.
public record CarDto(
        UUID id,
        @NotBlank String matricula,
        @NotNull @Min(1900) Integer ano,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotBlank String placa,
        @NotNull Owner proprietario) {

    public static CarDto from(Car c) {
        return new CarDto(c.getId(), c.getMatricula(), c.getAno(), c.getMarca(), c.getModelo(), c.getPlaca(), c.getProprietario());
    }

    public void copyTo(Car c) {
        c.setMatricula(matricula); c.setAno(ano); c.setMarca(marca);
        c.setModelo(modelo); c.setPlaca(placa); c.setProprietario(proprietario);
    }
}