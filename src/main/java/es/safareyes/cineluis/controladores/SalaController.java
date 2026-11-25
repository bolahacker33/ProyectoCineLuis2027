package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.modelos.Sala;
import es.safareyes.cineluis.repositorios.ISalaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salas")
public class SalaController {

    private final ISalaRepository salaRepository;

    public SalaController(ISalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TAQUILLA')")
    public List<Sala> listar() {
        return salaRepository.findAll(Sort.by("numero"));
    }
}
