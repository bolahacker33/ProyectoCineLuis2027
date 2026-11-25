package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.OcupacionDTO;
import es.safareyes.cineluis.dto.RecaudacionDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.repositorios.CompraRepository;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.repositorios.SesionRepository;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class InformeController {

    private final PeliculaRepository peliculaRepository;
    private final SesionRepository sesionRepository;
    private final CompraRepository compraRepository;

    @GetMapping("/peliculas/{id}/recaudacion")
    @PreAuthorize("hasRole('ADMIN')")
    public RecaudacionDTO recaudacion(@PathVariable Integer id) {
        Pelicula pelicula = peliculaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada"));

        Long entradas = compraRepository.sumarEntradasConfirmadasPorPelicula(id);
        BigDecimal total = compraRepository.sumarRecaudacionPorPelicula(id);

        return new RecaudacionDTO(
                id,
                pelicula.getTitulo(),
                entradas == null ? 0L : entradas,
                total == null ? BigDecimal.ZERO : total
        );
    }

    @GetMapping("/informes/ocupacion")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OcupacionDTO> ocupacion(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return sesionRepository.buscarSesiones(fecha, null).stream().map(s -> {
            int capacidad = s.getSala().getCapacidad();
            long vendidas = compraRepository.contarEntradasConfirmadas(s.getId());

            BigDecimal porcentaje = capacidad == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(vendidas).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(capacidad), 2, RoundingMode.HALF_UP);

            return new OcupacionDTO(
                    s.getId(),
                    s.getPelicula().getTitulo(),
                    s.getSala().getNumero(),
                    s.getFechaHoraInicio(),
                    capacidad,
                    vendidas,
                    porcentaje
            );
        }).toList();
    }
}
