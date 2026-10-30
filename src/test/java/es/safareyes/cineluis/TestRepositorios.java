package es.safareyes.cineluis;

import es.safareyes.cineluis.dto.GeneroDTO;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.repositorios.GeneroRepository;
import es.safareyes.cineluis.repositorios.PeliculaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TestRepositorios {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Test
    void testPeliculaRepository() {
        List<Pelicula> peliculas = peliculaRepository.buscarPorTitulo("%m%");
    }

    @Test
    void testBuscarPorId() {
        Optional<Pelicula> pelicula = peliculaRepository.buscarPorId(1);
    }

    @Test
    void testBuscarGenerosConNumeroPeliculas() {
        List<GeneroDTO> generos = generoRepository.buscarGenerosConNumeroPeliculas();
    }
}