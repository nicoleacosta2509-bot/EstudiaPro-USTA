package co.edu.usta.estudiapro.horario;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public final class HorarioDtos {

  private HorarioDtos() {}

  public record HorarioRequest(
      @NotNull(message = "Elige la asignatura.") Long asignaturaId,
      @NotNull(message = "Elige el día.") DiaSemana dia,
      @NotNull(message = "Escribe la hora de inicio.") @JsonFormat(pattern = "HH:mm")
          LocalTime horaInicio,
      @NotNull(message = "Escribe la hora de fin.") @JsonFormat(pattern = "HH:mm")
          LocalTime horaFin,
      @Size(max = 60, message = "El salón no puede pasar de 60 caracteres.") String salon) {}

  public record HorarioDto(
      Long id,
      Long asignaturaId,
      String asignaturaNombre,
      String color,
      DiaSemana dia,
      @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
      @JsonFormat(pattern = "HH:mm") LocalTime horaFin,
      String salon) {

    public static HorarioDto de(Horario h) {
      return new HorarioDto(
          h.getId(),
          h.getAsignatura().getId(),
          h.getAsignatura().getNombre(),
          h.getAsignatura().getColor(),
          h.getDia(),
          h.getHoraInicio(),
          h.getHoraFin(),
          h.getSalon());
    }
  }
}
