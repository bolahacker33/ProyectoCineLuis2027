package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.conversores.CompraMapper;
import es.safareyes.cineluis.excepciones.ReglaNegocioException;
import es.safareyes.cineluis.excepciones.ElementoNoEncontrado;
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
public class CompraService implements ICompraService {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private final ICompraRepository compraRepository;
    private final ISesionRepository sesionRepository;
    private final IUsuarioRepository usuarioRepository;
    private final CompraMapper compraMapper;

    @Transactional(readOnly = true)
    @Override
    public CompraDTO obtenerPorCodigo(String codigo, String username, boolean soloPropias) {
        Compra compra = compraRepository.findByCodCompra(codigo.toUpperCase())
                .orElseThrow(() -> new ElementoNoEncontrado("Compra no encontrada"));

        if (soloPropias && !compra.getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver compras de otro usuario");
        }
        return compraMapper.convertirADTO(compra);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CompraDTO> listar(int pagina, int tamanio, String username, boolean soloPropias) {
        Pageable pageable = PageRequest.of(
                Math.max(pagina, 0),
                Math.min(Math.max(tamanio, 1), 100),
                Sort.by(Sort.Direction.DESC, "fechaCompra"));

        Page<Compra> resultado = soloPropias
                ? compraRepository.findByUsuarioUsername(username, pageable)
                : compraRepository.findAll(pageable);

        return resultado.map(compraMapper::convertirADTO);
    }

    @Transactional
    @Override
    public CompraDTO crear(CompraRequest request, String username) {
        if (request.getSesionId() == null || request.getNumEntradas() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId y numEntradas son obligatorios");
        }
        if (request.getNumEntradas() < 1 || request.getNumEntradas() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se pueden comprar entre 1 y 10 entradas");
        }

        Sesion sesion = sesionRepository.buscarParaCompra(request.getSesionId())
                .orElseThrow(() -> new ElementoNoEncontrado("Sesión no encontrada"));

        if (!sesion.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La sesión ya ha empezado");
        }

        long vendidas = compraRepository.contarEntradasVendidas(sesion.getId());
        long libres = sesion.getSala().getCapacidad() - vendidas;
        if (request.getNumEntradas() > libres) {
            throw new ReglaNegocioException("No hay suficientes asientos libres. Quedan " + Math.max(libres, 0));
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

        return compraMapper.convertirADTO(compraRepository.save(compra));
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
}
