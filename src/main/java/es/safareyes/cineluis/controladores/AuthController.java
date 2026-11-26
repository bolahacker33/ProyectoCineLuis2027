package es.safareyes.cineluis.controladores;

import es.safareyes.cineluis.modelos.Usuario;
import es.safareyes.cineluis.repositorios.IUsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public record RegistroRequest(String username, String password) {
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        String rol = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        return Map.of("username", authentication.getName(), "rol", rol);
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registro(@RequestBody RegistroRequest request) {
        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().length() < 4) {
            return ResponseEntity.badRequest().build();
        }
        if (usuarioRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol("CLIENTE");
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("username", usuario.getUsername(), "rol", usuario.getRol()));
    }
}
