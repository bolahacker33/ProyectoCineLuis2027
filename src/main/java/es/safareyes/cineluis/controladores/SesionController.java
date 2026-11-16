package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.SesionDTO;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.CompraRepository;
import es.safareyes.cineluis.repositorios.SesionRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sesiones")
public class SesionController {

    private final SesionRepository sesionRepository;
    private final CompraRepository compraRepository;

    public SesionController(SesionRepository sesionRepository, CompraRepository compraRepository) {
        this.sesionRepository = sesionRepository;
        this.compraRepository = compraRepository;
    }

    // ENDPOINT 9: listado de sesiones con filtros
    @GetMapping
    public List<SesionDTO> obtenerSesiones(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer peliculaId) {

        return sesionRepository.buscarSesiones(fecha, peliculaId)
                .stream()
                .map(this::convertirDTO)
                .toList();
    }

    // ENDPOINT 10: detalle de sesión y precio del día
    @GetMapping("/{id}")
    public SesionDTO obtenerSesionPorId(@PathVariable Integer id) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        return convertirDTO(sesion);
    }

    private SesionDTO convertirDTO(Sesion sesion) {
        Integer capacidad = sesion.getSala().getCapacidad();
        Long vendidas = compraRepository.contarEntradasVendidas(sesion.getId());
        int libres = Math.max(0, capacidad - vendidas.intValue());

        return new SesionDTO(
                sesion.getId(),
                sesion.getPelicula().getTitulo(),
                sesion.getSala().getNumero(),
                capacidad,
                sesion.getPrecioBase(),
                sesion.calcularPrecioUnitario(),
                sesion.getFechaHoraInicio(),
                sesion.getFechaHoraFin(),
                libres
        );
    }
}
