package com.helpdesk.api.controller;

import com.helpdesk.api.dto.AscenderRequest;
import com.helpdesk.api.dto.UsuarioResponse;
import com.helpdesk.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService usuarioService;

    @PostMapping("/soporte")
    public ResponseEntity<UsuarioResponse> ascenderASoporte(@Valid @RequestBody AscenderRequest request) {
        return ResponseEntity.ok(usuarioService.ascenderASoporte(request));
    }
}