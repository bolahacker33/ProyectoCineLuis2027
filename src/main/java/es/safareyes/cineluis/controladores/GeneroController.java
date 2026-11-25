package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dtos.GeneroDTO;
import es.safareyes.cineluis.repositorios.IGeneroRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/generos")
public class GeneroController {

    private final IGeneroRepository generoRepository;

    public GeneroController(IGeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    @GetMapping
    public List<GeneroDTO> listarGeneros() {
        return generoRepository.buscarGenerosConNumeroPeliculas();
    }
}
