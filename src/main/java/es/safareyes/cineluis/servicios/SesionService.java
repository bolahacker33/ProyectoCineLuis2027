package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.conversores.SesionMapper;
import es.safareyes.cineluis.dtos.SesionDTO;
import java.time.LocalDate;
import java.util.List;
import es.safareyes.cineluis.excepciones.ReglaNegocioException;
import es.safareyes.cineluis.excepciones.ElementoDuplicado;
import es.safareyes.cineluis.excepciones.ElementoNoEncontrado;
import es.safareyes.cineluis.dtos.CrearSesionDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.Sala;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.IPeliculaRepository;
import es.safareyes.cineluis.repositorios.ISalaRepository;
import es.safareyes.cineluis.repositorios.ISesionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SesionService implements ISesionService {

    private ISesionRepository sesionRepository;
    private IPeliculaRepository peliculaRepository;
    private ISalaRepository salaRepository;
    private SesionMapper sesionMapper;

    private Sesion guardar(CrearSesionDTO request) {
        if (request.peliculaId() == null || request.salaId() == null
                || request.fechaHoraInicio() == null || request.precioBase() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "peliculaId, salaId, fechaHoraInicio y precioBase son obligatorios");
        }
        if (request.precioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio base debe ser mayor que 0");
        }

        Pelicula pelicula = peliculaRepository.findById(request.peliculaId())
                .orElseThrow(() -> new ElementoNoEncontrado("Película no encontrada"));
        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() -> new ElementoNoEncontrado("Sala no encontrada"));

        if (!pelicula.isCartelera()) {
            throw new ReglaNegocioException("La película no está en cartelera");
        }

        LocalDateTime inicio = request.fechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(pelicula.getDuracion());

        if (sesionRepository.contarSolapadas(sala.getId(), inicio, fin) > 0) {
            throw new ElementoDuplicado("La sala ya tiene una sesión en ese horario");
        }

        Sesion sesion = new Sesion();
        sesion.setPelicula(pelicula);
        sesion.setSala(sala);
        sesion.setFechaHoraInicio(inicio);
        sesion.setFechaHoraFin(fin);
        sesion.setPrecioBase(request.precioBase());
        return sesionRepository.save(sesion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionDTO> obtenerSesiones(LocalDate fecha, Integer peliculaId) {
        return sesionRepository.buscarSesiones(fecha, peliculaId)
                .stream()
                .map(sesionMapper::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SesionDTO obtenerSesionPorId(Integer id) {
        Sesion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new ElementoNoEncontrado("Sesión no encontrada"));
        return sesionMapper.convertirADTO(sesion);
    }

    @Override
    @Transactional
    public SesionDTO crear(CrearSesionDTO request) {
        return sesionMapper.convertirADTO(guardar(request));
    }
}
