package es.safareyes.cineluis.conversores;

import es.safareyes.cineluis.dtos.SesionDTO;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.ICompraRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SesionMapper {

    private final ICompraRepository compraRepository;

    public SesionDTO convertirADTO(Sesion sesion) {
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
