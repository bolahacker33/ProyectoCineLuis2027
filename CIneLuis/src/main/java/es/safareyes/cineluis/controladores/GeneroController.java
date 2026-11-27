package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dtos.GeneroDTO;
import es.safareyes.cineluis.servicios.IGeneroService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/generos")
public class GeneroController {

    private final IGeneroService generoService;

    @GetMapping
    public List<GeneroDTO> listarGeneros() {
        return generoService.listarGeneros();
    }
}
