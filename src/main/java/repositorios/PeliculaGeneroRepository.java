package repositorios;

import es.safareyes.cineluis.modelos.PeliculaGenero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaGeneroRepository extends JpaRepository<PeliculaGenero, PeliculaGenero.PeliculaGeneroId> {
}
