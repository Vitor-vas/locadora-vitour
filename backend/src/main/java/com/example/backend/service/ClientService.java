package com.example.backend.service;

import com.example.backend.dto.ClientDto;
import com.example.backend.dto.EmployerDto;
import com.example.backend.model.AppUser;
import com.example.backend.model.Client;
import com.example.backend.model.Role;
import com.example.backend.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

// @Transactional é necessário: open-in-view está desligado e a lista de empregadores é carregada sob demanda.
@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clients;

    @Transactional(readOnly = true)
    public List<ClientDto> list() {
        return clients.findAll().stream().map(ClientDto::from).toList();
    }

    @Transactional(readOnly = true)
    public ClientDto get(UUID id, AppUser caller) {
        assertCanAccess(id, caller);
        return ClientDto.from(find(id));
    }

    public ClientDto create(ClientDto dto) {
        if (clients.existsByCpf(dto.cpf()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        return ClientDto.from(clients.save(dto.toEntity()));
    }

    public ClientDto update(UUID id, ClientDto dto, AppUser caller) {
        assertCanAccess(id, caller);
        Client c = find(id);
        if (!c.getCpf().equals(dto.cpf()) && clients.existsByCpf(dto.cpf()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        c.setRg(dto.rg()); c.setCpf(dto.cpf()); c.setNome(dto.nome());
        c.setEndereco(dto.endereco()); c.setProfissao(dto.profissao());
        c.getEmpregadores().clear();
        c.getEmpregadores().addAll(dto.empregadores().stream().map(EmployerDto::toEntity).toList());
        return ClientDto.from(c); // alterações são gravadas ao final da transação
    }

    public void delete(UUID id) {
        clients.delete(find(id)); // falha com 409 se houver usuário ou pedidos vinculados
    }

    private Client find(UUID id) {
        return clients.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    // Agente acessa qualquer cliente; cliente só acessa o próprio perfil.
    private void assertCanAccess(UUID id, AppUser caller) {
        if (caller.getRole() == Role.AGENTE) return;
        if (caller.getClient() == null || !caller.getClient().getId().equals(id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
    }
}
