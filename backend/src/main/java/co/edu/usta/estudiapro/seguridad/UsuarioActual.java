package co.edu.usta.estudiapro.seguridad;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// Atajo para saber qué estudiante hace la petición (lo pone JwtFiltro)
public final class UsuarioActual {

  private UsuarioActual() {}

  public static Long id() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof Long id)) {
      throw new IllegalStateException("No hay un usuario autenticado en la petición");
    }
    return id;
  }
}
