package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.SesionDTO;
import es.safareyes.cineluis.dtos.SesionRequest;

import java.time.LocalDate;
import java.util.List;

public interface ISesionService {

    List<SesionDTO> obtenerSesiones(LocalDate fecha, Integer peliculaId);

    SesionDTO obtenerSesionPorId(Integer id);

    SesionDTO crear(SesionRequest request);
}
