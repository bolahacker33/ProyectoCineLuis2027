package es.safareyes.cineluis.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CrearPeliculaDTO(

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotNull(message = "El año es obligatorio")
        Integer anio,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración debe estar entre 1 y 300 minutos")
        @Max(value = 300, message = "La duración debe estar entre 1 y 300 minutos")
        Integer duracion,

        @NotBlank(message = "La clasificación es obligatoria")
        @Pattern(regexp = "TP|MAYORES_7|MAYORES_12|MAYORES_16|MAYORES_18",
                message = "Clasificación no válida. Valores: TP, MAYORES_7, MAYORES_12, MAYORES_16, MAYORES_18")
        String clasificacion,

        String sinopsis,

        @NotEmpty(message = "La película debe tener al menos un género")
        List<String> generos

) {
}
