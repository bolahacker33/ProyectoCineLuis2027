package es.safareyes.cineluis.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CrearCompraDTO(

        @NotNull(message = "La sesión es obligatoria")
        Integer sesionId,

        @NotNull(message = "El número de entradas es obligatorio")
        @Min(value = 1, message = "Se pueden comprar entre 1 y 10 entradas")
        @Max(value = 10, message = "Se pueden comprar entre 1 y 10 entradas")
        Integer numEntradas,

        Boolean parkingUsado

) {
}
