package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.model.AppUser;
import com.example.backend.model.Client;
import com.example.backend.model.Role;
import com.example.backend.repository.AppUserRepository;
import com.example.backend.repository.ClientRepository;
import com.example.backend.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository users;
    private final ClientRepository clients;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthResponse login(LoginRequest r) {
        AppUser u = users.findByUsername(r.username())
                .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos"));
        return new AuthResponse(tokens.generate(u), UserResponse.from(u));
    }

    // Cadastro público: sempre cria um usuário CLIENTE com seu perfil de cliente.
    @Transactional
    public UserResponse register(RegisterRequest r) {
        if (users.existsByUsername(r.username()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe");
        if (clients.existsByCpf(r.cliente().cpf()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        Client client = clients.save(r.cliente().toEntity());
        AppUser u = new AppUser();
        u.setUsername(r.username());
        u.setPasswordHash(encoder.encode(r.password()));
        u.setRole(Role.CLIENTE);
        u.setClient(client);
        return UserResponse.from(users.save(u));
    }
}
