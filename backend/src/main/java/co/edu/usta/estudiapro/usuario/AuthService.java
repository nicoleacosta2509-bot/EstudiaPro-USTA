package co.edu.usta.estudiapro.usuario;

import co.edu.usta.estudiapro.comun.ConflictoException;
import co.edu.usta.estudiapro.comun.NoEncontradoException;
import co.edu.usta.estudiapro.seguridad.JwtService;
import co.edu.usta.estudiapro.usuario.AuthDtos.AuthResponse;
import co.edu.usta.estudiapro.usuario.AuthDtos.LoginRequest;
import co.edu.usta.estudiapro.usuario.AuthDtos.RegistroRequest;
import co.edu.usta.estudiapro.usuario.AuthDtos.UsuarioDto;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final Clock clock;

  public AuthService(
      UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt, Clock clock) {
    this.usuarios = usuarios;
    this.encoder = encoder;
    this.jwt = jwt;
    this.clock = clock;
  }

  @Transactional
  public AuthResponse registrar(RegistroRequest req) {
    String email = normalizar(req.email());
    if (usuarios.existsByEmail(email)) {
      throw new ConflictoException("Ya existe una cuenta con ese correo.");
    }
    Usuario u =
        usuarios.save(
            new Usuario(
                req.nombre().trim(),
                email,
                encoder.encode(req.password()),
                LocalDateTime.now(clock)));
    return new AuthResponse(jwt.generar(u), UsuarioDto.de(u));
  }

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest req) {
    // Mismo mensaje si el correo no existe o la clave está mal, para no revelar cuentas
    Usuario u =
        usuarios
            .findByEmail(normalizar(req.email()))
            .filter(x -> encoder.matches(req.password(), x.getPasswordHash()))
            .orElseThrow(() -> new BadCredentialsException("credenciales"));
    return new AuthResponse(jwt.generar(u), UsuarioDto.de(u));
  }

  @Transactional(readOnly = true)
  public UsuarioDto yo(Long usuarioId) {
    return usuarios
        .findById(usuarioId)
        .map(UsuarioDto::de)
        .orElseThrow(() -> new NoEncontradoException("La cuenta ya no existe."));
  }

  private String normalizar(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }
}
