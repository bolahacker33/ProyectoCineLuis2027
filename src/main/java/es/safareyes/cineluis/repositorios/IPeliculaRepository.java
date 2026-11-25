package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPeliculaRepository extends JpaRepository<Pelicula, Integer> {

    @Query("SELECT p FROM Pelicula p")
    Page<Pelicula> buscarTodas(Pageable pageable);

    @Query("SELECT p FROM Pelicula p WHERE p.titulo LIKE :titulo")
    List<Pelicula> buscarPorTitulo(@Param("titulo") String titulo);

    @Query("SELECT p FROM Pelicula p WHERE p.id = :id")
    Optional<Pelicula> buscarPorId(@Param("id") Integer id);

    @Query(value = """
        SELECT p FROM Pelicula p
        WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
        AND (:filtrarCartelera = false OR p.cartelera = :cartelera)
        AND (:genero = '' OR EXISTS (
            SELECT 1 FROM PeliculaGenero pg, Genero g
            WHERE pg.peliculaId = p.id AND g.id = pg.generoId
            AND LOWER(g.nombre) = LOWER(:genero)))
        """,
            countQuery = """
        SELECT COUNT(p) FROM Pelicula p
        WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
        AND (:filtrarCartelera = false OR p.cartelera = :cartelera)
        AND (:genero = '' OR EXISTS (
            SELECT 1 FROM PeliculaGenero pg, Genero g
            WHERE pg.peliculaId = p.id AND g.id = pg.generoId
            AND LOWER(g.nombre) = LOWER(:genero)))
        """)
    Page<Pelicula> buscarConFiltros(@Param("titulo") String titulo,
                                    @Param("genero") String genero,
                                    @Param("filtrarCartelera") boolean filtrarCartelera,
                                    @Param("cartelera") boolean cartelera,
                                    Pageable pageable);
}
