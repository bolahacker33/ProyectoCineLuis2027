package es.safareyes.cineluis.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PeliculaRequest {

    private String titulo;
    private Integer anio;
    private Integer duracion;
    private String clasificacion;
    private String sinopsis;
    private List<String> generos;

}
