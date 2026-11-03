package com.example.backend.dto;

import com.example.backend.model.AgentType;
import com.example.backend.model.AppUser;
import com.example.backend.model.Role;

import java.util.UUID;

// Nunca expõe a senha/hash. Mesmo formato do tipo User do front.
public record UserResponse(UUID id, String username, Role role, AgentType agentType, UUID clientId) {
    public static UserResponse from(AppUser u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getRole(), u.getAgentType(),
                u.getClient() == null ? null : u.getClient().getId());
    }
}
