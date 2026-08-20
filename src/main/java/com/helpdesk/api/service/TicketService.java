package com.helpdesk.api.service;

import com.helpdesk.api.dto.CambiarEstadoRequest;
import com.helpdesk.api.dto.TicketRequest;
import com.helpdesk.api.dto.TicketResponse;
import com.helpdesk.api.entity.Estado;
import com.helpdesk.api.entity.Ticket;
import com.helpdesk.api.entity.Usuario;
import com.helpdesk.api.exception.ResourceNotFoundException;
import com.helpdesk.api.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    @Transactional
    public TicketResponse crear(TicketRequest request, Usuario creador) {
        LocalDateTime ahora = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .prioridad(request.getPrioridad())
                .estado(Estado.ABIERTO)
                .creadoEn(ahora)
                .slaVenceEn(ahora.plusHours(request.getPrioridad().getHorasSla()))
                .creadoPor(creador)
                .build();

        ticket = ticketRepository.save(ticket);
        return toResponse(ticket);
    }

    public List<TicketResponse> misTickets(Usuario usuario) {
        return ticketRepository.findByCreadoPor(usuario).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TicketResponse> listarTodos() {
        return ticketRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TicketResponse> listarVencidos() {
        return ticketRepository.findByEstadoNotAndSlaVenceEnBefore(Estado.RESUELTO, LocalDateTime.now()).stream()
                .map(this::toResponse)
                .toList();
    }

    public TicketResponse obtenerPorId(Long id, Usuario usuarioAutenticado) {
        Ticket ticket = buscarOFallar(id);

        boolean esDueno = ticket.getCreadoPor().getId().equals(usuarioAutenticado.getId());
        boolean esSoporteOAdmin = usuarioAutenticado.getRol().name().equals("SOPORTE")
                || usuarioAutenticado.getRol().name().equals("ADMIN");

        if (!esDueno && !esSoporteOAdmin) {
            throw new AccessDeniedException("No tiene permisos para ver este ticket");
        }

        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse cambiarEstado(Long id, CambiarEstadoRequest request) {
        Ticket ticket = buscarOFallar(id);
        ticket.setEstado(request.getEstado());
        ticket = ticketRepository.save(ticket);
        return toResponse(ticket);
    }

    private Ticket buscarOFallar(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id " + id));
    }

    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .titulo(ticket.getTitulo())
                .descripcion(ticket.getDescripcion())
                .prioridad(ticket.getPrioridad())
                .estado(ticket.getEstado())
                .creadoEn(ticket.getCreadoEn())
                .slaVenceEn(ticket.getSlaVenceEn())
                .vencido(ticket.isVencido())
                .creadoPorEmail(ticket.getCreadoPor().getEmail())
                .build();
    }
}