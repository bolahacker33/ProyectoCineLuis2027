package es.safareyes.cineluis.controladores;

import lombok.AllArgsConstructor;
import es.safareyes.cineluis.excepciones.ElementoNoEncontrado;
import es.safareyes.cineluis.dtos.SesionDTO;
import es.safareyes.cineluis.dtos.SesionRequest;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.conversores.SesionMapper;
import es.safareyes.cineluis.repositorios.ISesionRepository;
import es.safareyes.cineluis.servicios.ISesionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sesiones")
public class SesionController {

    private final ISesionRepository sesionRepository;
    private final SesionMapper sesionMapper;
    private final ISesionService sesionService;

    @GetMapping
    public List<SesionDTO> obtenerSesiones(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer peliculaId) {

        return sesionRepository.buscarSesiones(fecha, peliculaId)
                .stream()
                .map(sesionMapper::convertirADTO)
                .toList();
    }

    @GetMapping("/{id}")
    public SesionDTO obtenerSesionPorId(@PathVariable Integer id) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("Sesión no encontrada"));
        return sesionMapper.convertirADTO(sesion);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SesionDTO crear(@RequestBody SesionRequest request) {
        return sesionMapper.convertirADTO(sesionService.crear(request));
    }
}