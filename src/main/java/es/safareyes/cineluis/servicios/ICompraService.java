package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.CompraDTO;
import es.safareyes.cineluis.dtos.CompraRequest;
import org.springframework.data.domain.Page;

public interface ICompraService {

    CompraDTO obtenerPorCodigo(String codigo, String username, boolean soloPropias);

    Page<CompraDTO> listar(int pagina, int tamanio, String username, boolean soloPropias);

    CompraDTO crear(CompraRequest request, String username);
}
