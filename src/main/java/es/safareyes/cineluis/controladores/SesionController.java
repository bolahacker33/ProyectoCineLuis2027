package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.SesionDTO;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.CompraRepository;
import es.safareyes.cineluis.repositorios.SesionRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sesiones")
public class SesionController {

    private final SesionRepository sesionRepository;
    private final CompraRepository compraRepository;

    public SesionController(
            SesionRepository sesionRepository,
            CompraRepository compraRepository) {
        this.sesionRepository = sesionRepository;
        this.compraRepository = compraRepository;
    }

    // ENDPOINT 9: Listado de sesiones con filtros
    @GetMapping
    public List<SesionDTO> obtenerSesiones(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,

            @RequestParam(required = false)
            Integer peliculaId) {

        List<Sesion> sesiones =
                sesionRepository.buscarSesiones(fecha, peliculaId);

        return sesiones.stream().map(sesion -> {

            Integer capacidad = sesion.getSala().getCapacidad();

            Long entradasVendidas =
                    compraRepository.contarEntradasVendidas(sesion.getId());

            int libres = capacidad - entradasVendidas.intValue();

            return new SesionDTO(
                    sesion.getId(),
                    sesion.getPelicula().getTitulo(),
                    sesion.getSala().getNumero(),
                    capacidad,
                    sesion.getPrecioBase(),
                    sesion.getFechaHoraInicio(),
                    sesion.getFechaHoraFin(),
                    Math.max(0, libres)
            );

        }).toList();
    }

    // ENDPOINT 10: Detalle de una sesión por ID
    @GetMapping("/{id}")
    public SesionDTO obtenerSesionPorId(@PathVariable Integer id) {

        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Sesión no encontrada"));

        Integer capacidad = sesion.getSala().getCapacidad();

        Long entradasVendidas =
                compraRepository.contarEntradasVendidas(sesion.getId());

        int libres = capacidad - entradasVendidas.intValue();

        return new SesionDTO(
                sesion.getId(),
                sesion.getPelicula().getTitulo(),
                sesion.getSala().getNumero(),
                capacidad,
                sesion.getPrecioBase(),
                sesion.getFechaHoraInicio(),
                sesion.getFechaHoraFin(),
                Math.max(0, libres)
        );
    }
}