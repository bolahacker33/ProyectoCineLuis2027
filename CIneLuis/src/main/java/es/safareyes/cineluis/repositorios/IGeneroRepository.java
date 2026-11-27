package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.dtos.GeneroDTO;
import es.safareyes.cineluis.modelos.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IGeneroRepository extends JpaRepository<Genero, Integer> {

    @Query("SELECT new es.safareyes.cineluis.dtos.GeneroDTO(" +
            "g.nombre, COUNT(DISTINCT p.id)) " +
            "FROM Genero g " +
            "LEFT JOIN PeliculaGenero pg ON pg.generoId = g.id " +
            "LEFT JOIN Pelicula p ON p.id = pg.peliculaId AND p.cartelera = true " +
            "GROUP BY g.nombre " +
            "ORDER BY g.nombre")
    List<GeneroDTO> buscarGenerosConNumeroPeliculas();

    Optional<Genero> findByNombreIgnoreCase(String nombre);
}
