package es.safareyes.cineluis.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CrearSesionDTO(

        @NotNull(message = "La película es obligatoria")
        Integer peliculaId,

        @NotNull(message = "La sala es obligatoria")
        Integer salaId,

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,

        @NotNull(message = "El precio base es obligatorio")
        @Positive(message = "El precio base debe ser mayor que 0")
        BigDecimal precioBase

) {
}
