package es.safareyes.cineluis.conversores;

import es.safareyes.cineluis.dtos.CompraDTO;
import es.safareyes.cineluis.modelos.Compra;
import es.safareyes.cineluis.modelos.Sesion;
import org.springframework.stereotype.Component;

@Component
public class CompraMapper {

    public CompraDTO convertirADTO(Compra c) {
        Sesion s = c.getSesion();
        return new CompraDTO(
                c.getCodCompra(),
                s.getId(),
                s.getPelicula().getTitulo(),
                s.getSala().getNumero(),
                s.getFechaHoraInicio(),
                c.getNumEntradas(),
                c.getPrecioTotal(),
                c.getEstado(),
                c.getParkingUsado(),
                c.getFechaCompra(),
                c.getUsuario().getUsername()
        );
    }
}
