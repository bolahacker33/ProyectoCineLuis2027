package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.SesionRequest;
import es.safareyes.cineluis.modelos.Sesion;

public interface ISesionService {

    public Sesion crear(SesionRequest request);
}
