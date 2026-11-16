package es.safareyes.cineluis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionDTO {
    private Integer sesionId;
    private String pelicula;
    private Integer sala;
    private LocalDateTime fechaHoraInicio;
    private Integer capacidad;
    private Long entradasVendidas;
    private BigDecimal porcentajeOcupacion;
}
