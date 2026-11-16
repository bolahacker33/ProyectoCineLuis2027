package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dto.PeliculaDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.PeliculaGenero;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.repositorios.PeliculaGeneroRepository;
import es.safareyes.cineluis.repositorios.GeneroRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

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

    private PeliculaDTO convertirDTO(Pelicula pelicula) {

        List<String> generos = peliculaGeneroRepository
                .findByPeliculaId(pelicula.getId())
                .stream()
                .map(PeliculaGenero::getGeneroId)
                .map(id -> generoRepository.findById(id)
                        .map(g -> g.getNombre()).orElse(""))
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
