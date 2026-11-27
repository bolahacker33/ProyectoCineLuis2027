package es.safareyes.cineluis.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expiracionMinutos;

    public JwtService(JwtEncoder encoder,
                      @Value("${app.jwt.expiracion-minutos}") long expiracionMinutos) {
        this.encoder = encoder;
        this.expiracionMinutos = expiracionMinutos;
    }

    public long getExpiracionSegundos() {
        return expiracionMinutos * 60;
    }

    public String generar(String username, String rol) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cineluis")
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES))
                .subject(username)
                .claim("rol", rol)
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }
}
