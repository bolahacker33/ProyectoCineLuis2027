package es.safareyes.cineluis.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SesionRequest {

    private Integer peliculaId;
    private Integer salaId;
    private LocalDateTime fechaHoraInicio;
    private BigDecimal precioBase;

}
