package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.OcupacionDTO;
import es.safareyes.cineluis.dtos.RecaudacionDTO;
import es.safareyes.cineluis.excepciones.ElementoNoEncontrado;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.repositorios.ICompraRepository;
import es.safareyes.cineluis.repositorios.IPeliculaRepository;
import es.safareyes.cineluis.repositorios.ISesionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class InformeService implements IInformeService {

    private final IPeliculaRepository peliculaRepository;
    private final ISesionRepository sesionRepository;
    private final ICompraRepository compraRepository;

    @Override
    @Transactional(readOnly = true)
    public RecaudacionDTO recaudacion(Integer peliculaId) {
        Pelicula pelicula = peliculaRepository.findById(peliculaId)
                .orElseThrow(() -> new ElementoNoEncontrado("Película no encontrada"));

        Long entradas = compraRepository.sumarEntradasConfirmadasPorPelicula(peliculaId);
        BigDecimal total = compraRepository.sumarRecaudacionPorPelicula(peliculaId);

        return new RecaudacionDTO(
                peliculaId,
                pelicula.getTitulo(),
                entradas == null ? 0L : entradas,
                total == null ? BigDecimal.ZERO : total
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OcupacionDTO> ocupacion(LocalDate fecha) {
        return sesionRepository.buscarSesiones(fecha, null).stream().map(s -> {
            int capacidad = s.getSala().getCapacidad();
            long vendidas = compraRepository.contarEntradasConfirmadas(s.getId());

            BigDecimal porcentaje = capacidad == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(vendidas).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(capacidad), 2, RoundingMode.HALF_UP);

            return new OcupacionDTO(
                    s.getId(),
                    s.getPelicula().getTitulo(),
                    s.getSala().getNumero(),
                    s.getFechaHoraInicio(),
                    capacidad,
                    vendidas,
                    porcentaje
            );
        }).toList();
    }
}
