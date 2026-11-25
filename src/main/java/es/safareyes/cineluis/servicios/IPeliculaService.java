package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.PeliculaDTO;
import es.safareyes.cineluis.dtos.PeliculaRequest;
import org.springframework.data.domain.*;

public interface IPeliculaService {

    Page<PeliculaDTO> consultarTodasPeliculas(int pagina, int tamanio);

    Page<PeliculaDTO> consultarPeliculas(String titulo, String genero, Boolean cartelera, int pagina, int tamanio);

    PeliculaDTO consultarPorId(Integer id);

    PeliculaDTO crear(PeliculaRequest request);
}
