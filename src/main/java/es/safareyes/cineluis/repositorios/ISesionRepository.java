package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Sesion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISesionRepository extends JpaRepository<Sesion, Integer> {

    @Query("""
        SELECT s FROM Sesion s
        JOIN FETCH s.pelicula
        JOIN FETCH s.sala
        WHERE s.fechaHoraInicio >= :desde
        AND s.fechaHoraInicio < :hasta
        AND (:todasLasPeliculas = true OR s.pelicula.id = :peliculaId)
        ORDER BY s.fechaHoraInicio
        """)
    List<Sesion> buscarEntre(@Param("desde") LocalDateTime desde,
                             @Param("hasta") LocalDateTime hasta,
                             @Param("todasLasPeliculas") boolean todasLasPeliculas,
                             @Param("peliculaId") Integer peliculaId);

    @Query("""
        SELECT COUNT(s) FROM Sesion s
        WHERE s.sala.id = :salaId
        AND s.fechaHoraInicio < :fin
        AND s.fechaHoraFin > :inicio
        """)
    Long contarSolapadas(@Param("salaId") Integer salaId,
                         @Param("inicio") LocalDateTime inicio,
                         @Param("fin") LocalDateTime fin);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sesion s JOIN FETCH s.pelicula JOIN FETCH s.sala WHERE s.id = :id")
    Optional<Sesion> buscarParaCompra(@Param("id") Integer id);

    default List<Sesion> buscarSesiones(LocalDate fecha, Integer peliculaId) {
        LocalDateTime desde = fecha == null ? LocalDateTime.of(1900, 1, 1, 0, 0) : fecha.atStartOfDay();
        LocalDateTime hasta = fecha == null ? LocalDateTime.of(9999, 12, 31, 0, 0) : fecha.plusDays(1).atStartOfDay();
        return buscarEntre(desde, hasta, peliculaId == null, peliculaId == null ? -1 : peliculaId);
    }
}
