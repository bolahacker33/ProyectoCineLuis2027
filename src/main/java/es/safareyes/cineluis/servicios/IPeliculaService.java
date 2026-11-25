package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.PeliculaDTO;
import es.safareyes.cineluis.dtos.PeliculaRequest;
import org.springframework.data.domain.*;

public interface IPeliculaService {

    public Page<PeliculaDTO> consultarTodasPeliculas(int pagina, int tamanio);

    public Page<PeliculaDTO> consultarPeliculas(String titulo, String genero, Boolean cartelera, int pagina, int tamanio);

    public PeliculaDTO consultarPorId(Integer id);

    public PeliculaDTO crear(PeliculaRequest request);
}
