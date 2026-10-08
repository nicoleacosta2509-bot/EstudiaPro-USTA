package co.edu.usta.estudiapro.usuario;

import co.edu.usta.estudiapro.seguridad.UsuarioActual;
import co.edu.usta.estudiapro.usuario.AuthDtos.AuthResponse;
import co.edu.usta.estudiapro.usuario.AuthDtos.LoginRequest;
import co.edu.usta.estudiapro.usuario.AuthDtos.RegistroRequest;
import co.edu.usta.estudiapro.usuario.AuthDtos.UsuarioDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService auth;

  public AuthController(AuthService auth) {
    this.auth = auth;
  }

  @PostMapping("/registro")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
    return auth.registrar(req);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    return auth.login(req);
  }

  @GetMapping("/yo")
  public UsuarioDto yo() {
    return auth.yo(UsuarioActual.id());
  }
}
