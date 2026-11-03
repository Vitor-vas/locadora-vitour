package com.example.backend.controller;

import com.example.backend.dto.CarDto;
import com.example.backend.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService service;

    // Qualquer usuário logado consulta (o cliente precisa escolher o carro no pedido).
    @GetMapping
    public List<CarDto> list() { return service.list(); }

    @GetMapping("/{id}")
    public CarDto get(@PathVariable UUID id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('AGENTE')")
    public CarDto create(@Valid @RequestBody CarDto dto) { return service.create(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AGENTE')")
    public CarDto update(@PathVariable UUID id, @Valid @RequestBody CarDto dto) { return service.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('AGENTE')")
    public void delete(@PathVariable UUID id) { service.delete(id); }
}