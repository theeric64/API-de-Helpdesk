package com.helpdesk.api.repository;

import com.helpdesk.api.entity.Estado;
import com.helpdesk.api.entity.Ticket;
import com.helpdesk.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreadoPor(Usuario usuario);
    List<Ticket> findByEstadoNotAndSlaVenceEnBefore(Estado estado, LocalDateTime fecha);
}