package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.modelos.Sala;
import es.safareyes.cineluis.servicios.ISalaService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/salas")
public class SalaController {

    private final ISalaService salaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TAQUILLA')")
    public List<Sala> listar() {
        return salaService.listar();
    }
}
