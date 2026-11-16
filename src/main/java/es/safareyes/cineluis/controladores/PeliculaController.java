package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.PeliculaDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.PeliculaGenero;
import es.safareyes.cineluis.repositorios.GeneroRepository;
import es.safareyes.cineluis.repositorios.PeliculaGeneroRepository;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.servicios.PeliculaService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/peliculas")
public class PeliculaController {

    private final PeliculaRepository peliculaRepository;
    private final PeliculaGeneroRepository peliculaGeneroRepository;
    private final GeneroRepository generoRepository;
    private final PeliculaService service;

    public PeliculaController(
            PeliculaRepository peliculaRepository,
            PeliculaGeneroRepository peliculaGeneroRepository,
            GeneroRepository generoRepository,
            PeliculaService service) {

        this.peliculaRepository = peliculaRepository;
        this.peliculaGeneroRepository = peliculaGeneroRepository;
        this.generoRepository = generoRepository;
        this.service = service;
    }

    // GET: LISTAR TODAS LAS PELICULAS
    @GetMapping
    public Page<PeliculaDTO> listarPeliculas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        if (pagina < 0 || tamanio < 1 || tamanio > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La página o el tamaño no son válidos"
            );
        }

        return service.consultarTodasPeliculas(pagina, tamanio);
    }

    // GET: BUSCAR PELICULA POR ID
    @GetMapping("/{id}")
    public PeliculaDTO obtenerPelicula(@PathVariable Integer id) {

        Pelicula pelicula = peliculaRepository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No se ha encontrado la película"
                ));

        return convertirDTO(pelicula);
    }

    // CONVERTIR PELICULA A DTO
    private PeliculaDTO convertirDTO(Pelicula pelicula) {

        List<String> nombresGeneros = peliculaGeneroRepository
                .findByPeliculaId(pelicula.getId())
                .stream()
                .map(PeliculaGenero::getGeneroId)
                .map(generoId -> generoRepository.findById(generoId))
                .filter(java.util.Optional::isPresent)
                .map(genero -> genero.get().getNombre())
                .toList();

        return new PeliculaDTO(
                pelicula.getId(),
                pelicula.getTitulo(),
                pelicula.getAnio(),
                pelicula.getDuracion(),
                pelicula.getClasificacion(),
                pelicula.getSinopsis(),
                pelicula.isCartelera(),
                nombresGeneros
        );
    }
}
