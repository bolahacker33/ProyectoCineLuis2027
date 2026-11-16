package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dto.CompraDTO;
import es.safareyes.cineluis.servicios.CompraService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    // ENDPOINT 13: listado paginado
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TAQUILLA','CLIENTE')")
    public Page<CompraDTO> listar(@RequestParam(defaultValue = "0") int pagina,
                                  @RequestParam(defaultValue = "10") int tamanio,
                                  Authentication auth) {
        return compraService.listar(pagina, tamanio, auth.getName(), esCliente(auth));
    }

    // ENDPOINT 12: detalle de compra
    @GetMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN','TAQUILLA','CLIENTE')")
    public CompraDTO detalle(@PathVariable String codigo, Authentication auth) {
        return compraService.obtenerPorCodigo(codigo, auth.getName(), esCliente(auth));
    }

    private boolean esCliente(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
    }
}
