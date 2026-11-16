package es.safareyes.cineluis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaDTO {

    private Integer id;
    private String titulo;
    private Integer anio;
    private Integer duracion;
    private String clasificacion;
    private String sinopsis;
    private boolean cartelera;
    private List<String> generos;

}