package co.edu.usta.estudiapro.asignatura;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AsignaturaDtos {

  private AsignaturaDtos() {}

  public record AsignaturaRequest(
      @NotBlank(message = "Escribe el nombre de la asignatura.")
          @Size(max = 80, message = "El nombre no puede pasar de 80 caracteres.")
          String nombre,
      @Size(max = 80, message = "El nombre del docente no puede pasar de 80 caracteres.")
          String docente,
      @Pattern(
              regexp = "^#[0-9A-Fa-f]{6}$",
              message = "El color debe tener el formato #RRGGBB.")
          String color) {}

  public record AsignaturaDto(Long id, String nombre, String docente, String color) {

    public static AsignaturaDto de(Asignatura a) {
      return new AsignaturaDto(a.getId(), a.getNombre(), a.getDocente(), a.getColor());
    }
  }
}
