package com.example.backend.dto;

import com.example.backend.model.Client;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Mesmo formato JSON do tipo Client do front. Será reutilizado no CRUD de clientes.
public record ClientDto(
        UUID id,
        @NotBlank String rg,
        @NotBlank String cpf,
        @NotBlank String nome,
        @NotBlank String endereco,
        @NotBlank String profissao,
        @NotNull @Size(max = 3, message = "no máximo 3 empregadores") List<@Valid EmployerDto> empregadores) {

    public static ClientDto from(Client c) {
        return new ClientDto(c.getId(), c.getRg(), c.getCpf(), c.getNome(), c.getEndereco(), c.getProfissao(),
                c.getEmpregadores().stream().map(EmployerDto::from).toList());
    }

    public Client toEntity() {
        Client c = new Client();
        c.setRg(rg); c.setCpf(cpf); c.setNome(nome); c.setEndereco(endereco); c.setProfissao(profissao);
        c.setEmpregadores(empregadores.stream().map(EmployerDto::toEntity).collect(Collectors.toCollection(ArrayList::new)));
        return c;
    }
}
