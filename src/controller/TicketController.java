package com.helpdesk.api.controller;

import com.helpdesk.api.dto.CambiarEstadoRequest;
import com.helpdesk.api.dto.TicketRequest;
import com.helpdesk.api.dto.TicketResponse;
import com.helpdesk.api.entity.Usuario;
import com.helpdesk.api.service.TicketService;
import com.helpdesk.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<TicketResponse> crear(@Valid @RequestBody TicketRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        TicketResponse response = ticketService.crear(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mios")
    public ResponseEntity<List<TicketResponse>> misTickets(Authentication authentication) {
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        return ResponseEntity.ok(ticketService.misTickets(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> obtenerPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        return ResponseEntity.ok(ticketService.obtenerPorId(id, usuario));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SOPORTE','ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarTodos() {
        return ResponseEntity.ok(ticketService.listarTodos());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('SOPORTE','ADMIN')")
    public ResponseEntity<TicketResponse> cambiarEstado(@PathVariable Long id,
                                                          @Valid @RequestBody CambiarEstadoRequest request) {
        return ResponseEntity.ok(ticketService.cambiarEstado(id, request));
    }

    @GetMapping("/vencidos")
    @PreAuthorize("hasAnyRole('SOPORTE','ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarVencidos() {
        return ResponseEntity.ok(ticketService.listarVencidos());
    }
}