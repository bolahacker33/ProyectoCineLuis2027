package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dto.PeliculaDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.PeliculaGenero;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.repositorios.PeliculaGeneroRepository;
import es.safareyes.cineluis.repositorios.GeneroRepository;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class PeliculaService {

    private PeliculaRepository repository;
    private PeliculaGeneroRepository peliculaGeneroRepository;
    private GeneroRepository generoRepository;

    public Page<PeliculaDTO> consultarTodasPeliculas(int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio);

        return repository.buscarTodas(pageable).map(this::convertirDTO);
    }

    // ENDPOINT 1: listado paginado con filtros (RF02)
    public Page<PeliculaDTO> consultarPeliculas(String titulo, String genero, Boolean cartelera,
                                                int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(
                Math.max(pagina, 0),
                Math.min(Math.max(tamanio, 1), 100),
                Sort.by("titulo"));

        String t = titulo == null ? "" : titulo.trim();
        String g = genero == null ? "" : genero.trim();

        return repository
                .buscarConFiltros(t, g, cartelera != null, cartelera != null && cartelera, pageable)
                .map(this::convertirDTO);
    }

    // ENDPOINT 2: detalle de una película con sus géneros
    public PeliculaDTO consultarPorId(Integer id) {
        Pelicula pelicula = repository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada"));
        return convertirDTO(pelicula);
    }

    private PeliculaDTO convertirDTO(Pelicula pelicula) {

        List<String> generos = peliculaGeneroRepository
                .findByPeliculaId(pelicula.getId())
                .stream()
                .map(PeliculaGenero::getGeneroId)
                .map(id -> generoRepository.findById(id)
                        .map(g -> g.getNombre()).orElse(""))
                .sorted()
                .toList();

        return new PeliculaDTO(
                pelicula.getId(),
                pelicula.getTitulo(),
                pelicula.getAnio(),
                pelicula.getDuracion(),
                pelicula.getClasificacion(),
                pelicula.getSinopsis(),
                pelicula.isCartelera(),
                generos
        );
    }
}
