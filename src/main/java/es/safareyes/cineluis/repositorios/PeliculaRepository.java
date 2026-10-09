package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

    @Query("SELECT p FROM Pelicula p WHERE p.titulo LIKE :titulo")
    List<Pelicula> buscarPorTitulo(@Param("titulo") String titulo);

    @Query("SELECT p FROM Pelicula p WHERE p.id = :id")
    Optional<Pelicula> buscarPorId(@Param("id") Integer id);

}