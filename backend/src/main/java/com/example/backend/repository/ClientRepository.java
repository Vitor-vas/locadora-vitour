package com.example.backend.repository;

import com.example.backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByCpf(String cpf);
}
