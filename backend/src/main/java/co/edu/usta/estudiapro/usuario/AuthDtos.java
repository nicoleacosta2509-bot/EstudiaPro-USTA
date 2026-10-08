package co.edu.usta.estudiapro.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos que entran y salen de /api/auth
public final class AuthDtos {

  private AuthDtos() {}

  public record RegistroRequest(
      @NotBlank(message = "Escribe tu nombre.")
          @Size(max = 80, message = "El nombre no puede pasar de 80 caracteres.")
          String nombre,
      @NotBlank(message = "Escribe tu correo.")
          @Email(message = "El correo no tiene un formato válido.")
          @Size(max = 120, message = "El correo no puede pasar de 120 caracteres.")
          String email,
      @NotBlank(message = "Escribe una contraseña.")
          @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres.")
          String password) {}

  public record LoginRequest(
      @NotBlank(message = "Escribe tu correo.") String email,
      @NotBlank(message = "Escribe tu contraseña.") String password) {}

  public record UsuarioDto(Long id, String nombre, String email) {

    public static UsuarioDto de(Usuario u) {
      return new UsuarioDto(u.getId(), u.getNombre(), u.getEmail());
    }
  }

  public record AuthResponse(String token, UsuarioDto usuario) {}
}
