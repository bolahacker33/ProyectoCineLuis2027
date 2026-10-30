package es.safareyes.cineluis.repositorios;

import es.safareyes.cineluis.modelos.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    @Query("""
        SELECT COALESCE(SUM(c.numEntradas), 0)
        FROM Compra c
        WHERE c.sesion.id = :sesionId
        AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
        """)
    Long contarEntradasVendidas(@Param("sesionId") Integer sesionId);
}