package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.dtos.CompraDTO;
import es.safareyes.cineluis.dtos.CompraRequest;
import es.safareyes.cineluis.servicios.ICompraService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {

    private final ICompraService compraService;

    public CompraController(ICompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TAQUILLA','CLIENTE')")
    public Page<CompraDTO> listar(@RequestParam(defaultValue = "0") int pagina,
                                  @RequestParam(defaultValue = "10") int tamanio,
                                  Authentication auth) {
        return compraService.listar(pagina, tamanio, auth.getName(), esCliente(auth));
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN','TAQUILLA','CLIENTE')")
    public CompraDTO detalle(@PathVariable String codigo, Authentication auth) {
        return compraService.obtenerPorCodigo(codigo, auth.getName(), esCliente(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENTE','TAQUILLA')")
    public CompraDTO crear(@RequestBody CompraRequest request, Authentication auth) {
        return compraService.crear(request, auth.getName());
    }

    private boolean esCliente(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
    }
}
