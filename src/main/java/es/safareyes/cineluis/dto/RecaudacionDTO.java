package es.safareyes.cineluis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecaudacionDTO {
    private Integer peliculaId;
    private String titulo;
    private Long entradasVendidas;
    private BigDecimal recaudacionTotal;
}
