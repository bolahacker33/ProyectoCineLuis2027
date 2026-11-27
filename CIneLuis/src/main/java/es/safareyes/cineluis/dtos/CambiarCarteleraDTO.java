package es.safareyes.cineluis.dtos;

import jakarta.validation.constraints.NotNull;

public record CambiarCarteleraDTO(

        @NotNull(message = "El campo cartelera es obligatorio")
        Boolean cartelera

) {
}
