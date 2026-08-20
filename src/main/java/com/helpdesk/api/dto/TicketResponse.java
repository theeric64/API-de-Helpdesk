package com.helpdesk.api.dto;

import com.helpdesk.api.entity.Estado;
import com.helpdesk.api.entity.Prioridad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;
    private LocalDateTime creadoEn;
    private LocalDateTime slaVenceEn;
    private boolean vencido;
    private String creadoPorEmail;
}