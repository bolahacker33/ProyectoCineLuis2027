package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.conversores.PeliculaMapper;
import es.safareyes.cineluis.excepciones.ElementoNoEncontrado;
import es.safareyes.cineluis.dtos.PeliculaDTO;
import es.safareyes.cineluis.dtos.CrearPeliculaDTO;
import es.safareyes.cineluis.modelos.Genero;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.PeliculaGenero;
import es.safareyes.cineluis.repositorios.IPeliculaRepository;
import es.safareyes.cineluis.repositorios.IPeliculaGeneroRepository;
import es.safareyes.cineluis.repositorios.IGeneroRepository;

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
public class PeliculaService implements IPeliculaService {

    private static final List<String> CLASIFICACIONES =
            List.of("TP", "MAYORES_7", "MAYORES_12", "MAYORES_16", "MAYORES_18");

    private IPeliculaRepository repository;
    private IPeliculaGeneroRepository peliculaGeneroRepository;
    private IGeneroRepository generoRepository;
    private PeliculaMapper peliculaMapper;

    @Override
    public Page<PeliculaDTO> consultarTodasPeliculas(int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio);

        return repository.buscarTodas(pageable).map(peliculaMapper::convertirADTO);
    }

    @Override
    public Page<PeliculaDTO> consultarPeliculas(String titulo, String genero, String clasificacion, Boolean cartelera,
                                                int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(
                Math.max(pagina, 0),
                Math.min(Math.max(tamanio, 1), 100),
                Sort.by("titulo"));

        String t = titulo == null ? "" : titulo.trim();
        String g = genero == null ? "" : genero.trim();
        String c = clasificacion == null ? "" : clasificacion.trim().toUpperCase();

        return repository
                .buscarConFiltros(t, g, c, cartelera != null, cartelera != null && cartelera, pageable)
                .map(peliculaMapper::convertirADTO);
    }

    @Override
    public PeliculaDTO consultarPorId(Integer id) {
        Pelicula pelicula = repository.buscarPorId(id)
                .orElseThrow(() -> new ElementoNoEncontrado("Película no encontrada"));
        return peliculaMapper.convertirADTO(pelicula);
    }

    @Transactional
    @Override
    public PeliculaDTO crear(CrearPeliculaDTO request) {
        validar(request);
        List<Genero> generos = buscarGeneros(request.generos());

        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(request.titulo().trim());
        pelicula.setAnio(request.anio());
        pelicula.setDuracion(request.duracion());
        pelicula.setClasificacion(request.clasificacion());
        pelicula.setSinopsis(request.sinopsis());
        Pelicula guardada = repository.save(pelicula);

        for (Genero genero : generos) {
            PeliculaGenero peliculaGenero = new PeliculaGenero();
            peliculaGenero.setPeliculaId(guardada.getId());
            peliculaGenero.setGeneroId(genero.getId());
            peliculaGeneroRepository.save(peliculaGenero);
        }

        return peliculaMapper.convertirADTO(guardada);
    }

    private void validar(CrearPeliculaDTO request) {
        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título es obligatorio");
        }
        if (request.anio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El año es obligatorio");
        }
        if (request.duracion() == null || request.duracion() < 1 || request.duracion() > 300) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración debe estar entre 1 y 300 minutos");
        }
        if (request.clasificacion() == null || !CLASIFICACIONES.contains(request.clasificacion())) {
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
}
