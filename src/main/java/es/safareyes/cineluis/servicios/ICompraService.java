package es.safareyes.cineluis.servicios;

import es.safareyes.cineluis.dtos.CompraDTO;
import es.safareyes.cineluis.dtos.CompraRequest;
import org.springframework.data.domain.Page;

public interface ICompraService {

    public CompraDTO obtenerPorCodigo(String codigo, String username, boolean soloPropias);

    public Page<CompraDTO> listar(int pagina, int tamanio, String username, boolean soloPropias);

    public CompraDTO crear(CompraRequest request, String username);
}
