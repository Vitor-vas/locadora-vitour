package com.example.backend.controller;

import com.example.backend.dto.OrderDto;
import com.example.backend.dto.OrderRequest;
import com.example.backend.security.CurrentUser;
import com.example.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// As permissões por papel/estado ficam no OrderService (dependem do dono e do status do pedido).
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;
    private final CurrentUser current;

    @GetMapping
    public List<OrderDto> list(@AuthenticationPrincipal Jwt jwt) { return service.list(current.of(jwt)); }

    @GetMapping("/{id}")
    public OrderDto get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) { return service.get(id, current.of(jwt)); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto create(@RequestBody OrderRequest r, @AuthenticationPrincipal Jwt jwt) { return service.create(r, current.of(jwt)); }

    @PutMapping("/{id}")
    public OrderDto update(@PathVariable UUID id, @RequestBody OrderRequest r, @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, r, current.of(jwt));
    }
}