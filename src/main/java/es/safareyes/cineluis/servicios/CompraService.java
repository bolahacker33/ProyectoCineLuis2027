package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dto.CompraDTO;
import es.safareyes.cineluis.modelos.Compra;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.CompraRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;

    // ENDPOINT 12: detalle por código (un cliente solo puede ver las suyas)
    @Transactional(readOnly = true)
    public CompraDTO obtenerPorCodigo(String codigo, String username, boolean soloPropias) {
        Compra compra = compraRepository.findByCodCompra(codigo.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));

        if (soloPropias && !compra.getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver compras de otro usuario");
        }
        return convertirDTO(compra);
    }

    // ENDPOINT 13: listado paginado (un cliente solo ve las suyas)
    @Transactional(readOnly = true)
    public Page<CompraDTO> listar(int pagina, int tamanio, String username, boolean soloPropias) {
        Pageable pageable = PageRequest.of(
                Math.max(pagina, 0),
                Math.min(Math.max(tamanio, 1), 100),
                Sort.by(Sort.Direction.DESC, "fechaCompra"));

        Page<Compra> resultado = soloPropias
                ? compraRepository.findByUsuarioUsername(username, pageable)
                : compraRepository.findAll(pageable);

        return resultado.map(this::convertirDTO);
    }

    private CompraDTO convertirDTO(Compra c) {
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
