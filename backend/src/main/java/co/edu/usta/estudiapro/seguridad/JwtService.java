package co.edu.usta.estudiapro.seguridad;

import co.edu.usta.estudiapro.usuario.Usuario;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Genera y valida el token de sesión. El "subject" es el id del estudiante.
@Service
public class JwtService {

  private final SecretKey clave;
  private final long expiracionMs;

  public JwtService(
      @Value("${app.jwt.secret}") String secreto,
      @Value("${app.jwt.expiration-ms}") long expiracionMs) {
    this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
    this.expiracionMs = expiracionMs;
  }

  public String generar(Usuario u) {
    Date ahora = new Date();
    return Jwts.builder()
        .subject(String.valueOf(u.getId()))
        .claim("nombre", u.getNombre())
        .issuedAt(ahora)
        .expiration(new Date(ahora.getTime() + expiracionMs))
        .signWith(clave)
        .compact();
  }

  // Devuelve el id del usuario si el token es válido y no ha vencido
  public Optional<Long> usuarioId(String token) {
    try {
      String sub =
          Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload().getSubject();
      return Optional.of(Long.valueOf(sub));
    } catch (JwtException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
