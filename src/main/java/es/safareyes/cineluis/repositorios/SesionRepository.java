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
        SELECT s
        FROM Sesion s
        JOIN FETCH s.pelicula
        JOIN FETCH s.sala
        WHERE (:fecha IS NULL OR CAST(s.fechaHoraInicio AS LocalDate) = :fecha)
        AND (:peliculaId IS NULL OR s.pelicula.id = :peliculaId)
        ORDER BY s.fechaHoraInicio
        """)
    List<Sesion> buscarSesiones(
            @Param("fecha") LocalDate fecha,
            @Param("peliculaId") Integer peliculaId
    );
}