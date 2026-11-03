package com.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// O front envia { username, password, cliente: {...} }.
public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6, message = "mínimo de 6 caracteres") String password,
        @NotNull @Valid ClientDto cliente) {}
