package com.helpdesk.api.service;

import com.helpdesk.api.entity.RefreshToken;
import com.helpdesk.api.entity.Usuario;
import com.helpdesk.api.exception.InvalidTokenException;
import com.helpdesk.api.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public RefreshToken crear(Usuario usuario) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiraEn(LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000))
                .revocado(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validarYObtener(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("El refresh token no existe"));

        if (Boolean.TRUE.equals(refreshToken.getRevocado())) {
            throw new InvalidTokenException("El refresh token fue revocado");
        }

        if (refreshToken.estaExpirado()) {
            throw new InvalidTokenException("El refresh token expiró");
        }

        return refreshToken;
    }

    public void revocar(RefreshToken refreshToken) {
        refreshToken.setRevocado(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void revocarTodosDelUsuario(Usuario usuario) {
        List<RefreshToken> activos = refreshTokenRepository.findByUsuarioAndRevocadoFalse(usuario);
        activos.forEach(rt -> rt.setRevocado(true));
        refreshTokenRepository.saveAll(activos);
    }
}