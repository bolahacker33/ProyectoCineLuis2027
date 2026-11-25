package es.safareyes.cineluis;

import es.safareyes.cineluis.dtos.GeneroDTO;
import es.safareyes.cineluis.modelos.Compra;
import es.safareyes.cineluis.modelos.Pelicula;
import es.safareyes.cineluis.modelos.Sala;
import es.safareyes.cineluis.modelos.Sesion;
import es.safareyes.cineluis.repositorios.CompraRepository;
import es.safareyes.cineluis.repositorios.GeneroRepository;
import es.safareyes.cineluis.repositorios.PeliculaRepository;
import es.safareyes.cineluis.repositorios.SalaRepository;
import es.safareyes.cineluis.repositorios.SesionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TestRepositorios {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private CompraRepository compraRepository;


    void testPeliculaRepository() {
        List<Pelicula> peliculas = peliculaRepository.buscarPorTitulo("%m%");
        for (Pelicula p : peliculas) {
            System.out.println("[GET 1] ID: " + p.getId() + " | Titulo: " + p.getTitulo());
        }
    }


    @Test
    void testBuscarConFiltros() {
        Page<Pelicula> peliculas = peliculaRepository.buscarConFiltros("", "", false, false, PageRequest.of(0, 10));
        for (Pelicula p : peliculas.getContent()) {
            System.out.println("[GET 1 filtros] ID: " + p.getId() + " | Titulo: " + p.getTitulo()
                    + " | Cartelera: " + p.isCartelera());
        }
    }

    @Test
    void testBuscarPorId() {
        Optional<Pelicula> pelicula = peliculaRepository.buscarPorId(1);
        pelicula.ifPresent(p -> System.out.println("[GET 2] ID: " + p.getId() + " | Titulo: " + p.getTitulo()
                + " | Anio: " + p.getAnio() + " | Duracion: " + p.getDuracion()
                + " | Clasificacion: " + p.getClasificacion()));
    }

    // GET 6: /generos
    @Test
    void testBuscarGenerosConNumeroPeliculas() {
        List<GeneroDTO> generos = generoRepository.buscarGenerosConNumeroPeliculas();
        for (GeneroDTO g : generos) {
            System.out.println("[GET 6] Genero: " + g.getNombre() + " | Peliculas en cartelera: " + g.getNumeroPeliculas());
        }
    }

    @Test
    void testBuscarSalas() {
        List<Sala> salas = salaRepository.findAll();
        for (Sala s : salas) {
            System.out.println("[GET 7] Sala: " + s.getNumero() + " | Capacidad: " + s.getCapacidad());
        }
    }

    @Test
    void testBuscarSesiones() {
        List<Sesion> sesiones = sesionRepository.buscarSesiones(null, null);
        for (Sesion s : sesiones) {
            System.out.println("[GET 9] ID: " + s.getId() + " | Pelicula: " + s.getPelicula().getTitulo()
                    + " | Sala: " + s.getSala().getNumero() + " | Inicio: " + s.getFechaHoraInicio()
                    + " | Precio base: " + s.getPrecioBase());
        }
    }

    @Test
    void testBuscarSesionPorId() {
        Optional<Sesion> sesion = sesionRepository.findById(1);
        sesion.ifPresent(s -> System.out.println("[GET 10] ID: " + s.getId()
                + " | Precio base: " + s.getPrecioBase()
                + " | Precio del dia: " + s.calcularPrecioUnitario()));
    }

    @Test
    void testBuscarCompraPorCodigo() {
        List<Compra> todas = compraRepository.findAll();
        if (!todas.isEmpty()) {
            Optional<Compra> compra = compraRepository.findByCodCompra(todas.get(0).getCodCompra());
            compra.ifPresent(c -> System.out.println("[GET 12] ID: " + c.getId() + " | Codigo: " + c.getCodCompra()
                    + " | Estado: " + c.getEstado() + " | Entradas: " + c.getNumEntradas()
                    + " | Total: " + c.getPrecioTotal()));
        }
    }

    @Test
    void testBuscarCompras() {
        Page<Compra> compras = compraRepository.findAll(PageRequest.of(0, 10));
        for (Compra c : compras.getContent()) {
            System.out.println("[GET 13] ID: " + c.getId() + " | Codigo: " + c.getCodCompra()
                    + " | Estado: " + c.getEstado());
        }
    }

    @Test
    void testRecaudacionPelicula() {
        Long entradas = compraRepository.sumarEntradasConfirmadasPorPelicula(1);
        BigDecimal recaudacion = compraRepository.sumarRecaudacionPorPelicula(1);
        System.out.println("[GET 15] Pelicula ID: 1 | Entradas: " + entradas + " | Recaudacion: " + recaudacion);
    }

    @Test
    void testOcupacionSesiones() {
        List<Sesion> sesiones = sesionRepository.buscarSesiones(LocalDate.now(), null);
        for (Sesion s : sesiones) {
            Long confirmadas = compraRepository.contarEntradasConfirmadas(s.getId());
            System.out.println("[GET 16] Sesion ID: " + s.getId() + " | Capacidad: " + s.getSala().getCapacidad()
                    + " | Entradas confirmadas: " + confirmadas);
        }
    }
}
