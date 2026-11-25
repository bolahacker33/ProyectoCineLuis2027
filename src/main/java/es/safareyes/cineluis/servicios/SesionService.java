package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.SesionRequest;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.Sala;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.repositorios.SalaRepository;
import es.safareyes.cineluis.repositorios.SesionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SesionService {

    private SesionRepository sesionRepository;
    private PeliculaRepository peliculaRepository;
    private SalaRepository salaRepository;

    @Transactional
    public Sesion crear(SesionRequest request) {
        if (request.getPeliculaId() == null || request.getSalaId() == null
                || request.getFechaHoraInicio() == null || request.getPrecioBase() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "peliculaId, salaId, fechaHoraInicio y precioBase son obligatorios");
        }
        if (request.getPrecioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio base debe ser mayor que 0");
        }

        Pelicula pelicula = peliculaRepository.findById(request.getPeliculaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada"));
        Sala sala = salaRepository.findById(request.getSalaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala no encontrada"));

        if (!pelicula.isCartelera()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "La película no está en cartelera");
        }

        LocalDateTime inicio = request.getFechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(pelicula.getDuracion());

        if (sesionRepository.contarSolapadas(sala.getId(), inicio, fin) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La sala ya tiene una sesión en ese horario");
        }

        Sesion sesion = new Sesion();
        sesion.setPelicula(pelicula);
        sesion.setSala(sala);
        sesion.setFechaHoraInicio(inicio);
        sesion.setFechaHoraFin(fin);
        sesion.setPrecioBase(request.getPrecioBase());
        return sesionRepository.save(sesion);
    }
}
