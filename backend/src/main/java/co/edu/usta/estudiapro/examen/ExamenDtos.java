package co.edu.usta.estudiapro.examen;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public final class ExamenDtos {

  private ExamenDtos() {}

  public record ExamenRequest(
      @NotBlank(message = "Escribe el nombre del examen.")
          @Size(max = 120, message = "El nombre no puede pasar de 120 caracteres.")
          String titulo,
      @Size(max = 1000, message = "Los temas no pueden pasar de 1000 caracteres.") String temas,
      Long asignaturaId,
      @NotNull(message = "Indica la fecha del examen.")
          @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
          LocalDateTime fecha,
      @Size(max = 120, message = "El lugar no puede pasar de 120 caracteres.") String lugar,
      @Min(value = 1, message = "El recordatorio debe ser al menos 1 minuto antes.")
          @Max(value = 43200, message = "El recordatorio no puede ser de más de 30 días antes.")
          Integer recordatorioMinutos) {}

  public record CambioEstadoRequest(@NotNull(message = "Indica el nuevo estado.") EstadoExamen estado) {}

  public record ExamenDto(
      Long id,
      String titulo,
      String temas,
      Long asignaturaId,
      String asignaturaNombre,
      String color,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fecha,
      String lugar,
      EstadoExamen estado,
      Integer recordatorioMinutos) {

    public static ExamenDto de(Examen e) {
      return new ExamenDto(
          e.getId(),
          e.getTitulo(),
          e.getTemas(),
          e.getAsignatura() == null ? null : e.getAsignatura().getId(),
          e.getAsignatura() == null ? null : e.getAsignatura().getNombre(),
          e.getAsignatura() == null ? null : e.getAsignatura().getColor(),
          e.getFecha(),
          e.getLugar(),
          e.getEstado(),
          e.getRecordatorioMinutos());
    }
  }
}
