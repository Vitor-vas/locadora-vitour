package com.example.backend.security;

import com.example.backend.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder encoder;

    // Gera um JWT assinado (HS256) válido por 8 horas, com o papel do usuário na claim "role".
    public String generate(AppUser u) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(u.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(8, ChronoUnit.HOURS))
                .claim("role", u.getRole().name())
                .claim("uid", u.getId().toString())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
