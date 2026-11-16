package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, Integer> {

    @Query("""
        SELECT s FROM Sesion s
        JOIN FETCH s.pelicula p
        JOIN FETCH s.sala
        WHERE (cast(:fecha as date) IS NULL OR cast(s.fechaHoraInicio as date) = cast(:fecha as date))
          AND (cast(:peliculaId as integer) IS NULL OR p.id = :peliculaId)
        ORDER BY s.fechaHoraInicio
        """)
    List<Sesion> buscarSesiones(@Param("fecha") LocalDate fecha,
                                @Param("peliculaId") Integer peliculaId);
}
