package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dtos.OcupacionDTO;
import es.safareyes.cineluis.dtos.RecaudacionDTO;
import es.safareyes.cineluis.servicios.IInformeService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class InformeController {

    private final IInformeService informeService;

    @GetMapping("/peliculas/{id}/recaudacion")
    @PreAuthorize("hasRole('ADMIN')")
    public RecaudacionDTO recaudacion(@PathVariable Integer id) {
        return informeService.recaudacion(id);
    }

    @GetMapping("/informes/ocupacion")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OcupacionDTO> ocupacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return informeService.ocupacion(fecha);
    }
}
