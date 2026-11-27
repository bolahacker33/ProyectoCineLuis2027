package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.OcupacionDTO;
import es.safareyes.cineluis.dtos.RecaudacionDTO;

import java.time.LocalDate;
import java.util.List;

public interface IInformeService {

    RecaudacionDTO recaudacion(Integer peliculaId);

    List<OcupacionDTO> ocupacion(LocalDate fecha);
}
