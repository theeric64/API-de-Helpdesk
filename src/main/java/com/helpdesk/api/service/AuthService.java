package com.helpdesk.api.service;

import com.helpdesk.api.dto.*;
import com.helpdesk.api.entity.RefreshToken;
import com.helpdesk.api.entity.Rol;
import com.helpdesk.api.entity.Usuario;
import com.helpdesk.api.exception.ConflictException;
import com.helpdesk.api.exception.InvalidTokenException;
import com.helpdesk.api.repository.UsuarioRepository;
import com.helpdesk.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Ya existe un usuario registrado con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.USUARIO)
                .build();

        usuario = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String accessToken = jwtUtil.generarAccessToken(usuario);
        RefreshToken refreshToken = refreshTokenService.crear(usuario);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public AuthResponse refrescar(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.validarYObtener(request.getRefreshToken());

        Usuario usuario = refreshToken.getUsuario();

        refreshTokenService.revocar(refreshToken);
        RefreshToken nuevoRefreshToken = refreshTokenService.crear(usuario);

        String nuevoAccessToken = jwtUtil.generarAccessToken(usuario);

        return AuthResponse.builder()
                .accessToken(nuevoAccessToken)
                .refreshToken(nuevoRefreshToken.getToken())
                .build();
    }

    @Transactional
    public void logout(String emailAutenticado, LogoutRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            RefreshToken refreshToken = refreshTokenService.validarYObtener(request.getRefreshToken());

            if (!refreshToken.getUsuario().getEmail().equals(emailAutenticado)) {
                throw new InvalidTokenException("El refresh token no pertenece al usuario autenticado");
            }

            refreshTokenService.revocar(refreshToken);
        } else {
            refreshTokenService.revocarTodosDelUsuario(usuario);
        }
    }
}