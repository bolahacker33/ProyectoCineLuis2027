package es.safareyes.cineluis.conversores;

import es.safareyes.cineluis.dtos.PeliculaDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.PeliculaGenero;
import es.safareyes.cineluis.repositorios.IGeneroRepository;
import es.safareyes.cineluis.repositorios.IPeliculaGeneroRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PeliculaMapper {

    private final IPeliculaGeneroRepository peliculaGeneroRepository;
    private final IGeneroRepository generoRepository;

    public PeliculaDTO convertirADTO(Pelicula pelicula) {
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
