package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dto.PeliculaDTO;
import es.safareyes.cineluis.dto.PeliculaRequest;
import es.safareyes.cineluis.modelos.Genero;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.PeliculaGenero;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.repositorios.PeliculaGeneroRepository;
import es.safareyes.cineluis.repositorios.GeneroRepository;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PeliculaService {

    private static final List<String> CLASIFICACIONES =
            List.of("TP", "MAYORES_7", "MAYORES_12", "MAYORES_16", "MAYORES_18");

    private PeliculaRepository repository;
    private PeliculaGeneroRepository peliculaGeneroRepository;
    private GeneroRepository generoRepository;

    public Page<PeliculaDTO> consultarTodasPeliculas(int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio);

        return repository.buscarTodas(pageable).map(this::convertirDTO);
    }

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

    public PeliculaDTO consultarPorId(Integer id) {
        Pelicula pelicula = repository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada"));
        return convertirDTO(pelicula);
    }

    @Transactional
    public PeliculaDTO crear(PeliculaRequest request) {
        validar(request);
        List<Genero> generos = buscarGeneros(request.getGeneros());

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(request.getTitulo().trim());
        pelicula.setAnio(request.getAnio());
        pelicula.setDuracion(request.getDuracion());
        pelicula.setClasificacion(request.getClasificacion());
        pelicula.setSinopsis(request.getSinopsis());
        Pelicula guardada = repository.save(pelicula);

        for (Genero genero : generos) {
            PeliculaGenero peliculaGenero = new PeliculaGenero();
            peliculaGenero.setPeliculaId(guardada.getId());
            peliculaGenero.setGeneroId(genero.getId());
            peliculaGeneroRepository.save(peliculaGenero);
        }

        return convertirDTO(guardada);
    }

    private void validar(PeliculaRequest request) {
        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título es obligatorio");
        }
        if (request.getAnio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año es obligatorio");
        }
        if (request.getDuracion() == null || request.getDuracion() < 1 || request.getDuracion() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración debe estar entre 1 y 300 minutos");
        }
        if (request.getClasificacion() == null || !CLASIFICACIONES.contains(request.getClasificacion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Clasificación no válida. Valores: " + CLASIFICACIONES);
        }
    }

    private List<Genero> buscarGeneros(List<String> nombres) {
        if (nombres == null || nombres.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La película debe tener al menos un género");
        }
        Map<Integer, Genero> generos = new LinkedHashMap<>();
        for (String nombre : nombres) {
            Genero genero = generoRepository.findByNombreIgnoreCase(nombre == null ? "" : nombre.trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El género no existe: " + nombre));
            generos.put(genero.getId(), genero);
        }
        return List.copyOf(generos.values());
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
