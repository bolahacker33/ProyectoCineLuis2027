package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.PeliculaGenero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPeliculaGeneroRepository extends JpaRepository<PeliculaGenero, PeliculaGenero.PeliculaGeneroId> {

    List<PeliculaGenero> findByPeliculaId(Integer peliculaId);
}
