package co.edu.usta.estudiapro.tarea;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public final class TareaDtos {

  private TareaDtos() {}

  public record TareaRequest(
      @NotBlank(message = "Escribe el título de la tarea.")
          @Size(max = 120, message = "El título no puede pasar de 120 caracteres.")
          String titulo,
      @Size(max = 1000, message = "La descripción no puede pasar de 1000 caracteres.")
          String descripcion,
      Long asignaturaId,
      @NotNull(message = "Indica la fecha de entrega.")
          @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
          LocalDateTime fechaEntrega,
      Prioridad prioridad,
      @Min(value = 1, message = "El recordatorio debe ser al menos 1 minuto antes.")
          @Max(value = 43200, message = "El recordatorio no puede ser de más de 30 días antes.")
          Integer recordatorioMinutos) {}

  public record CambioEstadoRequest(@NotNull(message = "Indica el nuevo estado.") EstadoTarea estado) {}

  public record TareaDto(
      Long id,
      String titulo,
      String descripcion,
      Long asignaturaId,
      String asignaturaNombre,
      String color,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaEntrega,
      Prioridad prioridad,
      EstadoTarea estado,
      boolean vencida,
      Integer recordatorioMinutos) {

    public static TareaDto de(Tarea t, LocalDateTime ahora) {
      boolean vencida =
          t.getEstado() != EstadoTarea.TERMINADA && t.getFechaEntrega().isBefore(ahora);
      return new TareaDto(
          t.getId(),
          t.getTitulo(),
          t.getDescripcion(),
          t.getAsignatura() == null ? null : t.getAsignatura().getId(),
          t.getAsignatura() == null ? null : t.getAsignatura().getNombre(),
          t.getAsignatura() == null ? null : t.getAsignatura().getColor(),
          t.getFechaEntrega(),
          t.getPrioridad(),
          t.getEstado(),
          vencida,
          t.getRecordatorioMinutos());
    }
  }
}
