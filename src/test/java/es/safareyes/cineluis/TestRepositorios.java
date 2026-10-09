package es.safareyes.cineluis;
import es.safareyes.cineluis.modelos.Pelicula;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestRepositorios {

    @Autowired
    private es.safareyes.cineluis.repositorios.PeliculaRepository peliculaRepository;

    @Test
     void testPeliculaRepository() {
        List<Pelicula> peliculas = peliculaRepository.buscarPorTitulo("%m%");

    }
}