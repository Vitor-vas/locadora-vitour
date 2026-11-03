package com.example.backend.security;

import com.example.backend.model.AppUser;
import com.example.backend.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

// Converte o token da requisição (subject = username) no usuário do banco.
@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final AppUserRepository users;

    public AppUser of(Jwt jwt) {
        return users.findByUsername(jwt.getSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida"));
    }
}
