package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.CompraDTO;
import es.safareyes.cineluis.dtos.CompraRequest;
import es.safareyes.cineluis.modelos.Compra;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.modelos.Usuario;
import es.safareyes.cineluis.repositorios.ICompraRepository;
import es.safareyes.cineluis.repositorios.ISesionRepository;
import es.safareyes.cineluis.repositorios.IUsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CompraService {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final ICompraRepository compraRepository;
    private final ISesionRepository sesionRepository;
    private final IUsuarioRepository usuarioRepository;

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

    @Transactional
    public CompraDTO crear(CompraRequest request, String username) {
        if (request.getSesionId() == null || request.getNumEntradas() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId y numEntradas son obligatorios");
        }
        if (request.getNumEntradas() < 1 || request.getNumEntradas() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se pueden comprar entre 1 y 10 entradas");
        }

        Sesion sesion = sesionRepository.buscarParaCompra(request.getSesionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        if (!sesion.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La sesión ya ha empezado");
        }

        long vendidas = compraRepository.contarEntradasVendidas(sesion.getId());
        long libres = sesion.getSala().getCapacidad() - vendidas;
        if (request.getNumEntradas() > libres) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay suficientes asientos libres. Quedan " + Math.max(libres, 0));
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        Compra compra = new Compra();
        compra.setCodCompra(generarCodigo());
        compra.setUsuario(usuario);
        compra.setSesion(sesion);
        compra.setNumEntradas(request.getNumEntradas());
        compra.setParkingUsado(Boolean.TRUE.equals(request.getParkingUsado()));
        compra.setFechaCompra(LocalDateTime.now());
        compra.setEstado("CONFIRMADA");
        compra.setPrecioTotal(sesion.calcularPrecioUnitario()
                .multiply(BigDecimal.valueOf(request.getNumEntradas())));

        return convertirDTO(compraRepository.save(compra));
    }

    private String generarCodigo() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                sb.append(ALFABETO.charAt(ALEATORIO.nextInt(ALFABETO.length())));
            }
            codigo = sb.toString();
        } while (compraRepository.findByCodCompra(codigo).isPresent());
        return codigo;
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
