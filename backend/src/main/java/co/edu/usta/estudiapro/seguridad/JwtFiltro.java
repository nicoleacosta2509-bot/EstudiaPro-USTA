package co.edu.usta.estudiapro.seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Lee "Authorization: Bearer <token>" y deja al estudiante autenticado para la petición
@Component
public class JwtFiltro extends OncePerRequestFilter {

  private final JwtService jwt;

  public JwtFiltro(JwtService jwt) {
    this.jwt = jwt;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      jwt.usuarioId(header.substring(7))
          .ifPresent(
              id ->
                  SecurityContextHolder.getContext()
                      .setAuthentication(
                          new UsernamePasswordAuthenticationToken(id, null, List.of())));
    }
    chain.doFilter(req, res);
  }
}
