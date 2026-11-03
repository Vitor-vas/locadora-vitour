package com.example.backend.controller;

import com.example.backend.dto.ClientDto;
import com.example.backend.security.CurrentUser;
import com.example.backend.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;
    private final CurrentUser current;

    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public List<ClientDto> list() { return service.list(); }

    @GetMapping("/{id}")
    public ClientDto get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return service.get(id, current.of(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AGENTE')")
    public ClientDto create(@Valid @RequestBody ClientDto dto) { return service.create(dto); }

    @PutMapping("/{id}")
    public ClientDto update(@PathVariable UUID id, @Valid @RequestBody ClientDto dto, @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, dto, current.of(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('AGENTE')")
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
