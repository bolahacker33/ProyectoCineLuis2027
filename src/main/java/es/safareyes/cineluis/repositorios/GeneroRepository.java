package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.dto.GeneroDTO;
import es.safareyes.cineluis.modelos.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Integer> {

    @Query("SELECT new es.safareyes.cineluis.dto.GeneroDTO(" +
            "g.nombre, COUNT(DISTINCT CASE WHEN s.id IS NOT NULL THEN p.id END)) " +
            "FROM Genero g " +
            "LEFT JOIN PeliculaGenero pg ON pg.generoId = g.id " +
            "LEFT JOIN Pelicula p ON p.id = pg.peliculaId " +
            "LEFT JOIN Sesion s ON s.pelicula.id = p.id " +
            "AND s.fechaHoraFin >= CURRENT_TIMESTAMP " +
            "GROUP BY g.nombre " +
            "ORDER BY g.nombre")
    List<GeneroDTO> buscarGenerosConNumeroPeliculas();
}