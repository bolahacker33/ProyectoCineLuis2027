package es.safareyes.cineluis.controladores;

import jakarta.validation.Valid;
import es.safareyes.cineluis.dtos.SesionDTO;
import es.safareyes.cineluis.dtos.CrearSesionDTO;
import es.safareyes.cineluis.servicios.ISesionService;
import lombok.AllArgsConstructor;
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

    private final ISesionService sesionService;

    @GetMapping
    public List<SesionDTO> obtenerSesiones(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer peliculaId) {
        return sesionService.obtenerSesiones(fecha, peliculaId);
    }

    @GetMapping("/{id}")
    public SesionDTO obtenerSesionPorId(@PathVariable Integer id) {
        return sesionService.obtenerSesionPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SesionDTO crear(@Valid @RequestBody CrearSesionDTO request) {
        return sesionService.crear(request);
    }
}
