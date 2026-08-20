package com.helpdesk.api.service;

import com.helpdesk.api.dto.AscenderRequest;
import com.helpdesk.api.dto.UsuarioResponse;
import com.helpdesk.api.entity.Rol;
import com.helpdesk.api.entity.Usuario;
import com.helpdesk.api.exception.ResourceNotFoundException;
import com.helpdesk.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
    }

    @Transactional
    public UsuarioResponse ascenderASoporte(AscenderRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario con email " + request.getEmail()));

        usuario.setRol(Rol.SOPORTE);
        usuario = usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }
}