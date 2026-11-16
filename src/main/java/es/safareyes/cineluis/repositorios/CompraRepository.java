package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    @Query("""
        SELECT COALESCE(SUM(c.numEntradas), 0)
        FROM Compra c
        WHERE c.sesion.id = :sesionId
        AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
        """)
    Long contarEntradasVendidas(@Param("sesionId") Integer sesionId);

    // RN-10: ocupación solo con compras CONFIRMADA
    @Query("""
        SELECT COALESCE(SUM(c.numEntradas), 0)
        FROM Compra c
        WHERE c.sesion.id = :sesionId AND c.estado = 'CONFIRMADA'
        """)
    Long contarEntradasConfirmadas(@Param("sesionId") Integer sesionId);

    // RN-10: recaudación solo con compras CONFIRMADA
    @Query("""
        SELECT SUM(c.numEntradas)
        FROM Compra c
        WHERE c.sesion.pelicula.id = :peliculaId AND c.estado = 'CONFIRMADA'
        """)
    Long sumarEntradasConfirmadasPorPelicula(@Param("peliculaId") Integer peliculaId);

    @Query("""
        SELECT SUM(c.precioTotal)
        FROM Compra c
        WHERE c.sesion.pelicula.id = :peliculaId AND c.estado = 'CONFIRMADA'
        """)
    BigDecimal sumarRecaudacionPorPelicula(@Param("peliculaId") Integer peliculaId);

    @EntityGraph(attributePaths = {"sesion", "sesion.pelicula", "sesion.sala", "usuario"})
    Optional<Compra> findByCodCompra(String codCompra);

    @EntityGraph(attributePaths = {"sesion", "sesion.pelicula", "sesion.sala", "usuario"})
    Page<Compra> findByUsuarioUsername(String username, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"sesion", "sesion.pelicula", "sesion.sala", "usuario"})
    Page<Compra> findAll(Pageable pageable);
}
