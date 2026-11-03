package com.example.backend.repository;

import com.example.backend.model.AgentType;
import com.example.backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<AppUser> findByAgentType(AgentType agentType); // lista de bancos para o crédito
}
