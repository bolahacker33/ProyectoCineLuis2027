package es.safareyes.cineluis.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompraRequest {

    private Integer sesionId;
    private Integer numEntradas;
    private Boolean parkingUsado;

}
