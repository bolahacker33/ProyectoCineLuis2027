package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.GeneroDTO;
import es.safareyes.cineluis.repositorios.GeneroRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/generos")
public class GeneroController {

    private final GeneroRepository generoRepository;

    public GeneroController(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    @GetMapping
    public List<GeneroDTO> listarGeneros() {
        return generoRepository.buscarGenerosConNumeroPeliculas();
    }
}
