package repositorios;


import es.safareyes.cineluis.modelos.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Usuario;

@Repository
public interface UsuarioRepository<integer> extends JpaRepository<Usuario, integer>{
}
