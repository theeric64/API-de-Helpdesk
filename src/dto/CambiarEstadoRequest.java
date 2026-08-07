package com.helpdesk.api.dto;

import com.helpdesk.api.entity.Estado;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private Estado estado;
}