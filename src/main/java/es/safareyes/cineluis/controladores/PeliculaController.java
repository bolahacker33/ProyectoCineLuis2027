package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.PeliculaDTO;
import es.safareyes.cineluis.dto.PeliculaRequest;
import es.safareyes.cineluis.servicios.PeliculaService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @GetMapping
    public Page<PeliculaDTO> listar(@RequestParam(required = false) String titulo,
                                    @RequestParam(required = false) String genero,
                                    @RequestParam(required = false) Boolean cartelera,
                                    @RequestParam(defaultValue = "0") int pagina,
                                    @RequestParam(defaultValue = "10") int tamanio) {
        return peliculaService.consultarPeliculas(titulo, genero, cartelera, pagina, tamanio);
    }

    @GetMapping("/{id}")
    public PeliculaDTO detalle(@PathVariable Integer id) {
        return peliculaService.consultarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeliculaDTO crear(@RequestBody PeliculaRequest request) {
        return peliculaService.crear(request);
    }
}
