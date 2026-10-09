package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

    @Query(value = "SELECT p FROM Pelicula p WHERE p.titulo LIKE :titulo")
    List<Pelicula> buscarPorTitulo(@Param("titulo") String titulo);


}
